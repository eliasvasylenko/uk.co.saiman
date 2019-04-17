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

import java.lang.reflect.AnnotatedType;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyValueConversion;
import uk.co.saiman.properties.PropertyValueConverter;

@Component
public class PropertyValueConverterService implements PropertyValueConverter {
  private final PropertyValueConverter converter = PropertyValueConverter
      .getDefaultValueConverter();

  @Override
  public PropertyValueConversion<?> getConversion(
      LocaleProvider localeProvider,
      PropertyResource propertyResource,
      AnnotatedType type,
      String key) {
    return converter.getConversion(localeProvider, propertyResource, type, key);
  }

}
