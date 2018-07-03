/*
 * Copyright (C) 2018 Elias N Vasylenko <eliasvasylenko@strangeskies.co.uk>
 *      __   _______  ____           _       __     _      __       __
 *    ,`_ `,|__   __||  _ `.        / \     |  \   | |  ,-`__`¬  ,-`__`¬
 *   ( (_`-'   | |   | | ) |       / . \    | . \  | | / .`  `' / .`  `'
 *    `._ `.   | |   | |<. L      / / \ \   | |\ \ | || |    _ | '--.
 *   _   `. \  | |   | |  `.`.   / /   \ \  | | \ \| || |   | || +--'
 *  \ \__.' /  | |   | |    \ \ / /     \ \ | |  \ ` | \ `._' | \ `.__,.
 *   `.__.-`   |_|   |_|    |_|/_/       \_\|_|   \__|  `-.__.J  `-.__.J
 *                   __    _         _      __      __
 *                 ,`_ `, | |  _    | |  ,-`__`¬  ,`_ `,
 *                ( (_`-' | | ) |   | | / .`  `' ( (_`-'
 *                 `._ `. | L-' L   | || '--.     `._ `.
 *                _   `. \| ,.-^.`. | || +--'    _   `. \
 *               \ \__.' /| |    \ \| | \ `.__,.\ \__.' /
 *                `.__.-` |_|    |_||_|  `-.__.J `.__.-`
 *
 * This file is part of uk.co.strangeskies.text.
 *
 * uk.co.strangeskies.text is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.strangeskies.text is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSet;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * Delegate implementation object for proxy instances of property accessor
 * interfaces. This class deals with most method interception from the proxies
 * generated by {@link PropertyLoader}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <A>
 *          the type of the delegating proxy
 */
public class PropertyAccessorDelegate<A> {
  private static final Set<Method> DIRECT_METHODS = getDirectMethods();

  private static Set<Method> getDirectMethods() {
    return unmodifiableSet(new HashSet<>(asList(Object.class.getDeclaredMethods())));
  }

  private static final Constructor<MethodHandles.Lookup> METHOD_HANDLE_CONSTRUCTOR = getMethodHandleConstructor();

  private static Constructor<Lookup> getMethodHandleConstructor() {
    try {
      Constructor<Lookup> constructor = MethodHandles.Lookup.class
          .getDeclaredConstructor(Class.class, int.class);

      if (!constructor.isAccessible()) {
        constructor.setAccessible(true);
      }

      return constructor;
    } catch (NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  private final PropertyLoaderImpl loader;
  private final Log log;
  private final Class<A> source;
  private final A proxy;

  private final PropertyResource propertyResource;
  private final Map<Method, PropertyValueDelegate<A>> valueDelegates = new ConcurrentHashMap<>();

  /**
   * @param loader
   *          which created the delegate, to call back to
   * @param propertyResource
   *          the resource for the properties backing the accessor
   * @param log
   *          the log, or null
   * @param source
   *          the property accessor class and configuration
   */
  public PropertyAccessorDelegate(
      PropertyLoaderImpl loader,
      PropertyResource propertyResource,
      Log log,
      Class<A> source) {
    this.loader = loader;
    this.log = Log.forwardingLog(log);
    this.source = source;
    this.propertyResource = propertyResource;

    if (!source.isInterface()) {
      PropertyLoaderException e = new PropertyLoaderException(getText().mustBeInterface(source));
      log.log(Level.ERROR, e);
      throw e;
    }

    proxy = createProxy(source);

    initialize();
  }

  PropertyLoader getLoader() {
    return loader;
  }

  Class<A> getSource() {
    return source;
  }

  private PropertyLoaderProperties getText() {
    return loader.getProperties();
  }

  private void initialize() {
    for (Method method : source.getMethods()) {
      if (!DIRECT_METHODS.contains(method) && !method.isDefault()) {
        loadPropertyValueDelegate(method);
      }
    }
  }

  private PropertyValueDelegate<A> loadPropertyValueDelegate(Method method) {
    return valueDelegates.computeIfAbsent(method, s -> new PropertyValueDelegate<>(this, s));
  }

  private Object getInstantiatedPropertyValue(Method method, Object... arguments) {
    List<?> argumentList;
    if (arguments == null) {
      argumentList = emptyList();
    } else {
      argumentList = asList(arguments);
    }

    try {
      return loadPropertyValueDelegate(method).getValue(argumentList);
    } catch (Exception e) {
      /*
       * Extra layer of protection for internal properties, so things can still
       * function if there is a problem retrieving them...
       */
      if (source.equals(PropertyLoaderProperties.class)) {
        try {
          return method.invoke(new DefaultPropertyLoaderProperties(), arguments);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
          throw new RuntimeException(e1);
        }
      } else {
        throw e;
      }
    }
  }

  @SuppressWarnings("unchecked")
  <T> Function<List<?>, T> parseValueString(AnnotatedType propertyType, String key, Locale locale) {
    PropertyValueLoader provider = null;

    try {
      String valueString = loadValueString(source, key, locale);

      return arguments -> (T) provider.load(propertyType, key, valueString, arguments);
    } catch (MissingResourceException e) {
      if (provider.providesDefault()) {
        return arguments -> (T) provider.getDefault(key, arguments);
      }
      PropertyLoaderException ple = new PropertyLoaderException(
          getText().translationNotFoundMessage(key),
          e);
      log.log(Level.WARN, ple);
      throw ple;
    }
  }

  private String loadValueString(Class<?> source, String key, Locale locale) {
    return propertyResource.getValue(key, locale);
  }

  @SuppressWarnings("unchecked")
  A createProxy(Class<A> accessor) {
    ClassLoader classLoader = new PropertyAccessorClassLoader(accessor.getClassLoader());

    return (A) Proxy
        .newProxyInstance(
            classLoader,
            new Class<?>[] { accessor },
            (Object p, Method method, Object[] args) -> {
              if (DIRECT_METHODS.contains(method)) {
                return method.invoke(PropertyAccessorDelegate.this, args);
              }

              if (method.isDefault()) {
                return METHOD_HANDLE_CONSTRUCTOR
                    .newInstance(method.getDeclaringClass(), MethodHandles.Lookup.PRIVATE)
                    .unreflectSpecial(method, method.getDeclaringClass())
                    .bindTo(p)
                    .invokeWithArguments(args);
              }

              return getInstantiatedPropertyValue(method, args);
            });
  }

  class PropertyAccessorClassLoader extends ClassLoader {
    public PropertyAccessorClassLoader(ClassLoader classLoader) {
      super(classLoader);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      if (name.equals(PropertyAccessorDelegate.class.getName())) {
        return PropertyAccessorDelegate.class;
      } else {
        return super.findClass(name);
      }
    }
  }

  public A getProxy() {
    return proxy;
  }
}
