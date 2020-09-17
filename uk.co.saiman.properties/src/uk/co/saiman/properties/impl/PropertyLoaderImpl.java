/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.properties.impl;

import static java.util.Objects.requireNonNull;

import uk.co.saiman.collection.computingmap.CacheComputingMap;
import uk.co.saiman.collection.computingmap.ComputingMap;
import uk.co.saiman.log.Log;
import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.properties.PropertyResourceLoader;
import uk.co.saiman.properties.PropertyValueConverter;

public class PropertyLoaderImpl implements PropertyLoader {
  private static class AccessorConfiguration {
    private Class<?> accessorClass;
    private ClassLoader classLoader;

    public AccessorConfiguration(Class<?> accessorClass, ClassLoader classLoader) {
      this.accessorClass = accessorClass;
      this.classLoader = classLoader;
    }
  }

  private final ComputingMap<AccessorConfiguration, Object> localizationCache;

  private final LocaleProvider locale;
  private final PropertyResourceLoader resourceLoader;
  private final PropertyValueConverter valueConverter;
  private final Log log;

  /**
   * Create a new {@link PropertyLoader} instance for the given initial locale.
   * 
   * @param locale
   *          the initial locale
   * @param log
   *          the log for localization
   */
  public PropertyLoaderImpl(
      LocaleProvider locale,
      PropertyResourceLoader resourceLoader,
      PropertyValueConverter valueConverter,
      Log log) {
    localizationCache = new CacheComputingMap<>(
        c -> new PropertyAccessorDelegate<>(this, c.accessorClass, c.classLoader).getProxy(),
        true);

    this.locale = locale;
    this.resourceLoader = resourceLoader;
    this.valueConverter = valueConverter;
    this.log = requireNonNull(log);
  }

  @Override
  public LocaleProvider getLocaleProvider() {
    return locale;
  }

  protected PropertyResourceLoader getResourceLoader() {
    return resourceLoader;
  }

  protected PropertyValueConverter getValueConverter() {
    return valueConverter;
  }

  protected Log getLog() {
    return log;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getProperties(Class<T> accessorConfiguration, ClassLoader classLoader) {
    return (T) localizationCache
        .putGet(new AccessorConfiguration(accessorConfiguration, classLoader));
  }
}
