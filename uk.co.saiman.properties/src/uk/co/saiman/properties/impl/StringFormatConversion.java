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
package uk.co.saiman.properties.impl;

import static java.lang.String.format;

import java.util.List;

import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyValueConversion;

public class StringFormatConversion implements PropertyValueConversion<String> {
  private final LocaleProvider localeProvider;
  private final PropertyResource propertyResource;
  private final String key;

  public StringFormatConversion(
      LocaleProvider localeProvider,
      PropertyResource propertyResource,
      String key) {
    this.localeProvider = localeProvider;
    this.propertyResource = propertyResource;
    this.key = key;
  }

  @Override
  public String applyConversion(List<?> arguments) {
    String value;
    try {
      value = format(
          propertyResource.getValue(key, localeProvider.getLocale()),
          arguments.toArray());
    } catch (Exception e) {
      value = format("<?%s?>%s", key, arguments);
    }
    return value;
  }
}
