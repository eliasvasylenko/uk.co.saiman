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
 * This file is part of uk.co.saiman.measurement.
 *
 * uk.co.saiman.measurement is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.measurement is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.measurement;

import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.measure.Quantity;

public interface QuantityFormat {
  Quantity<?> parse(CharSequence characters, ParsePosition cursor);

  default Quantity<?> parse(String quantity) {
    quantity = quantity.trim();
    ParsePosition position = new ParsePosition(0);
    Quantity<?> parsed = parse(quantity, position);
    if (position.getIndex() != quantity.length())
      throw new IllegalArgumentException(
          "Could not parse full string "
              + quantity
              + " "
              + position.getIndex()
              + " "
              + quantity.length());
    return parsed;
  }

  String format(Quantity<?> quantity);

  QuantityFormat withNumberFormat(NumberFormat format);

  QuantityFormat withUnitFormat(UnitFormat format);

  // TODO ? QuantityFormat withLocale(Locale locale);
}
