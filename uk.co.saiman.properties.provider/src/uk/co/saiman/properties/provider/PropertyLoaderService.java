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

import java.util.Locale;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.log.Log;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.PropertyLoader;

@Component
public class PropertyLoaderService implements PropertyLoader {
  private PropertyLoader propertyLoader;

  @Reference
  private LocaleProvider localeProvider;

  @Reference
  private Log log;

  @Activate // TODO constructor injection R7
  public void initialize() {
    propertyLoader = PropertyLoader.newPropertyLoader(localeProvider, log);
  }

  @Override
  public Locale getLocale() {
    return propertyLoader.getLocale();
  }

  @Override
  public ObservableValue<Locale> locale() {
    return propertyLoader.locale();
  }

  @Override
  public <T> T getProperties(Class<T> accessor) {
    return propertyLoader.getProperties(accessor);
  }
}
