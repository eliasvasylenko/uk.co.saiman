/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.eclipse.
 *
 * uk.co.saiman.eclipse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.utilities;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static uk.co.saiman.collection.StreamUtilities.streamNullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.IInjector;

import uk.co.saiman.collection.StreamUtilities;

public final class EclipseUtilities {
  private EclipseUtilities() {}

  public static void injectSupertypes(IEclipseContext providerContext, Class<?> key) {
    injectSupertypes(providerContext, key.getName());
  }

  @SuppressWarnings("unchecked")
  public static <T> void injectSupertypes(
      IEclipseContext providerContext,
      Class<T> key,
      Function<T, Object> derivationFunction) {
    injectSupertypes(providerContext, key.getName(), value -> derivationFunction.apply((T) value));
  }

  public static void injectSupertypes(IEclipseContext providerContext, String key) {
    injectSupertypes(providerContext, key, identity());
  }

  public static void injectSupertypes(
      IEclipseContext providerContext,
      String key,
      Function<Object, Object> derivationFunction) {
    injectDerived(
        providerContext,
        key,
        derivationFunction
            .andThen(
                object -> StreamUtilities
                    .<Class<?>>flatMapRecursive(
                        object.getClass(),
                        t -> concat(
                            streamNullable(t.getSuperclass()),
                            Stream.of(t.getInterfaces())))
                    .collect(toMap(Class::getName, k -> object))));
  }

  /**
   * Inject derived objects based on a single source object of the given key.
   * Derived objects are only available to child contexts if the source object is
   * still visible to them, i.e. is not overridden in a more specific context.
   * 
   * @param providerContext    the root context in which to inject the derivations
   * @param key                the key of the source object
   * @param derivationFunction a mapping from the value of a source object to a
   *                           map of keys to objects to be injected
   */
  public static void injectDerived(
      IEclipseContext providerContext,
      String key,
      Function<Object, Map<String, Object>> derivationFunction) {

    providerContext.runAndTrack(new RunAndTrack() {
      @Override
      public synchronized boolean changed(IEclipseContext context) {
        Object providerObject = context.get(key);
        if (providerObject != null) {
          var derivations = derivationFunction.apply(providerObject);

          IContextFunction configurationFunction = (requesterContext, k) -> {
            return Optional
                .ofNullable(requesterContext.get(key))
                .filter(requesterObject -> Objects.equals(requesterObject, providerObject))
                .flatMap(o -> Optional.ofNullable(derivations.get(k)))
                .orElse(IInjector.NOT_A_VALUE);
          };

          runExternalCode(() -> {
            derivations
                .keySet()
                .stream()
                .filter(c -> !c.equals(key))
                .forEach(c -> context.set(c, configurationFunction));
          });
        }

        return true;
      }
    });
  }

  public static boolean isModifiable(IEclipseContext context, Class<?> key) {
    return isModifiable(context, key.getName());
  }

  public static boolean isModifiable(IEclipseContext context, String key) {
    if (!context.containsKey(key)) {
      return false;
    }

    try {
      context.modify(key, context.get(key));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
