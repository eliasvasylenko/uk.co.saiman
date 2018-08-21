/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static uk.co.saiman.log.Log.discardingLog;
import static uk.co.saiman.properties.LocaleProvider.getDefaultProvider;
import static uk.co.saiman.properties.PropertyResourceLoader.getDefaultResourceLoader;
import static uk.co.saiman.properties.PropertyValueConverter.getDefaultValueConverter;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import uk.co.saiman.log.Log;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.properties.impl.PropertyLoaderImpl;

/**
 * This interface represents a simple but powerful system for
 * internationalization. Instances of this class provide automatic
 * implementations of sub-interfaces of {@link Properties} according to a
 * {@link Locale} setting, which delegate method invocations to be fetched from
 * {@link ResourceBundle resource bundles}.
 * 
 * <p>
 * A property accessor interface is an interface to provide an API over static
 * properties and localized text and data. Users should not implement this class
 * themselves, instead they should define sub-interfaces, allowing
 * implementations to be automatically provided by {@link PropertyLoader}.
 * <p>
 * A key is generated for each method based on the class and method name. The
 * key is generated according to the {@link Properties} used to load the
 * {@link Properties} instance.
 * <p>
 * Default and static methods will be invoked directly.
 * <p>
 * A {@link Properties} instance is {@link Observable} over changes to its
 * locale, with the instance itself being passed as the message to observers.
 * 
 * @author Elias N Vasylenko
 */
public interface PropertyLoader {
  PropertyLoader DEFAULT_PROPERTY_LOADER = newPropertyLoader(
      getDefaultProvider(),
      getDefaultResourceLoader(),
      getDefaultValueConverter(),
      discardingLog());

  LocaleProvider getLocaleProvider();

  /**
   * Generate an implementing instance of the given accessor interface class,
   * according to the rules described by {@link Properties}.
   * 
   * @param <T>
   *          the type of the localization text accessor interface
   * @param accessor
   *          the sub-interface of {@link Properties} we wish to implement
   * @return an implementation of the accessor interface
   */
  <T> T getProperties(Class<T> accessor);

  static PropertyLoader getDefaultPropertyLoader() {
    return DEFAULT_PROPERTY_LOADER;
  }

  /**
   * Get a simple {@link PropertyLoader} implementation over the given locale
   * provider.
   * 
   * @param localeProvider
   *          a provider to establish a locale setting
   * @param log
   *          a log for localization information
   * @return a {@link PropertyLoader} for the given locale
   */
  static PropertyLoader newPropertyLoader(
      LocaleProvider localeProvider,
      PropertyResourceLoader resourceLoader,
      PropertyValueConverter valueConverter,
      Log log) {
    return new PropertyLoaderImpl(localeProvider, resourceLoader, valueConverter, log);
  }
}
