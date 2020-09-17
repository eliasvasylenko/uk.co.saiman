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

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;

import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.Localized;
import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyValueConversion;
import uk.co.saiman.properties.PropertyValueConverter;

public class PropertyValueConverterImpl implements PropertyValueConverter {
  @Override
  public PropertyValueConversion<?> getConversion(
      LocaleProvider localeProvider,
      PropertyResource propertyResource,
      AnnotatedType type,
      String key) {
    requireNonNull(localeProvider);
    requireNonNull(propertyResource);
    requireNonNull(type);
    requireNonNull(key);

    if (type.getType() == String.class) {
      return new StringFormatConversion(localeProvider, propertyResource, key);
    }

    if (type instanceof AnnotatedParameterizedType) {
      AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType) type;
      Class<?> rawType = (Class<?>) ((ParameterizedType) type.getType()).getRawType();

      if (rawType == Localized.class) {
        return new LocalizedConversion(
            this,
            localeProvider,
            propertyResource,
            parameterizedType.getAnnotatedActualTypeArguments()[0],
            key);
      }
    }

    return null;
  }
}
