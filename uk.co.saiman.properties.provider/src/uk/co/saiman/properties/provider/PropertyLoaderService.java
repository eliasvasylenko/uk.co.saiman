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
 * This file is part of uk.co.saiman.properties.provider.
 *
 * uk.co.saiman.properties.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.properties.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties.provider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.log.Log;
import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.properties.PropertyResourceLoader;
import uk.co.saiman.properties.PropertyValueConverter;

@Component
public class PropertyLoaderService implements PropertyLoader {
  private final LocaleProvider localeProvider;
  private final PropertyLoader propertyLoader;

  @Activate
  public PropertyLoaderService(
      @Reference LocaleProvider localeProvider,
      @Reference PropertyResourceLoader resourceLoader,
      @Reference PropertyValueConverter valueConverter,
      @Reference Log log) {
    this.localeProvider = localeProvider;
    this.propertyLoader = PropertyLoader
        .newPropertyLoader(localeProvider, resourceLoader, valueConverter, log);
  }

  @Override
  public LocaleProvider getLocaleProvider() {
    return localeProvider;
  }

  @Override
  public <T> T getProperties(Class<T> accessor, ClassLoader classLoader) {
    return propertyLoader.getProperties(accessor);
  }

  @Override
  public <T> T getProperties(Class<T> accessor) {
    return propertyLoader.getProperties(accessor);
  }
}
