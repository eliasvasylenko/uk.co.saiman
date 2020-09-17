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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.measure.Quantity;
import javax.measure.Unit;

import tec.uom.se.AbstractUnit;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

class QuantityFormatImpl implements QuantityFormat {
  private final UnitFormat unitFormat;
  private final NumberFormat numberFormat;

  public QuantityFormatImpl() {
    this(new UnitFormatImpl(), new DecimalFormat());
  }

  public QuantityFormatImpl(UnitFormat unitFormat, NumberFormat numberFormat) {
    this.unitFormat = unitFormat;
    this.numberFormat = numberFormat;
  }

  @Override
  public String format(Quantity<?> quantity) {
    String string = this.numberFormat.format(quantity.getValue());
    if (quantity.getUnit().equals(AbstractUnit.ONE)) {
      return string;
    }
    return string + ' ' + this.unitFormat.format(quantity.getUnit());
  }

  @Override
  public ComparableQuantity<?> parse(CharSequence csq, ParsePosition cursor) {
    final String str = csq.toString();
    final Number number = this.numberFormat.parse(str, cursor);
    if (number == null) {
      throw new IllegalArgumentException("Number cannot be parsed");
    }
    final Unit<?> unit = this.unitFormat.parse(str, cursor);
    return Quantities.getQuantity(number, unit);
  }

  @Override
  public QuantityFormat withNumberFormat(NumberFormat numberFormat) {
    return new QuantityFormatImpl(unitFormat, numberFormat);
  }

  @Override
  public QuantityFormat withUnitFormat(UnitFormat unitFormat) {
    return new QuantityFormatImpl(unitFormat, numberFormat);
  }
}
