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
 * This file is part of uk.co.saiman.properties.
 *
 * uk.co.saiman.properties is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.properties is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties;

import static java.util.Arrays.asList;
import static java.util.Collections.list;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A simple {@link PropertyResource} implementation backed by one or more
 * {@link ResourceBundle resource bundles}.
 * 
 * @author Elias N Vasylenko
 */
public class PropertyResourceBundle implements PropertyResource {
  private static class ResourceBundleDescriptor {
    private final ClassLoader classLoader;
    private final String location;

    public ResourceBundleDescriptor(ClassLoader classLoader, String location) {
      this.classLoader = classLoader;
      this.location = location;
    }

    public ClassLoader getClassLoader() {
      return classLoader;
    }

    public String getLocation() {
      return location;
    }

    @Override
    public String toString() {
      return location + "(" + classLoader + ")";
    }
  }

  private static final String PROPERTIES_POSTFIX = "Properties";

  private final Class<?> accessor;
  private final List<ResourceBundleDescriptor> resources;
  private final Map<Locale, List<ResourceBundle>> localizedResourceBundles;

  public PropertyResourceBundle(Class<?> accessor) {
    this(accessor, accessor.getClassLoader(), asList(getDefaultResource(accessor)));
  }

  /**
   * Create a resource bundle with the given initial locale.
   * 
   * @param accessor  the accessor class type
   * @param resources the resource locations
   */
  public PropertyResourceBundle(
      Class<?> accessor,
      ClassLoader classLoader,
      Collection<? extends String> resources) {
    this.accessor = accessor;
    this.localizedResourceBundles = new HashMap<>();
    this.resources = resources
        .stream()
        .map(resource -> new ResourceBundleDescriptor(classLoader, resource))
        .collect(toList());
  }

  @Override
  public Set<String> getKeys(Locale locale) {
    Set<String> keys = new LinkedHashSet<>();

    for (ResourceBundle bundle : getResourceBundles(locale)) {
      keys.addAll(list(bundle.getKeys()));
    }

    return keys;
  }

  @Override
  public String getValue(String key, Locale locale) {
    for (ResourceBundle bundle : getResourceBundles(locale)) {
      try {
        return bundle.getString(key);
      } catch (MissingResourceException e) {}
    }

    throw new MissingResourceException(
        "Cannot find resources for key "
            + key
            + " in locale "
            + locale
            + " in any of "
            + resources
            + " for "
            + accessor,
        accessor.toString(),
        key);
  }

  protected synchronized List<ResourceBundle> getResourceBundles(Locale locale) {
    if (localizedResourceBundles.containsKey(locale)) {
      return localizedResourceBundles.get(locale);
    } else {
      List<ResourceBundle> resourceBundles = new ArrayList<>();
      localizedResourceBundles.put(locale, resourceBundles);

      for (ResourceBundleDescriptor resource : resources) {
        try {
          resourceBundles
              .add(
                  ResourceBundle
                      .getBundle(resource.getLocation(), locale, resource.getClassLoader()));
        } catch (MissingResourceException e) {}
      }

      return resourceBundles;
    }
  }

  /**
   * @param name the string to remove the postfix from
   * @return the given string, with the simple class name {@link Properties}
   *         removed from the end, if present.
   */
  private static String removePropertiesPostfix(String name) {
    if (name.endsWith(PROPERTIES_POSTFIX) && name.length() > PROPERTIES_POSTFIX.length()) {
      name = name.substring(0, name.length() - PROPERTIES_POSTFIX.length());
    }

    return name;
  }

  public static String getDefaultResource(Class<?> accessor) {
    uk.co.saiman.properties.Properties properties = accessor
        .getAnnotation(uk.co.saiman.properties.Properties.class);

    String resource = "";
    if (properties != null) {
      resource = properties.path();
    }
    if (resource.isEmpty()) {
      resource = removePropertiesPostfix(accessor.getName());
    }
    return resource;
  }
}
