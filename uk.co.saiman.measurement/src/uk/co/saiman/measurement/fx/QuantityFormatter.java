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
package uk.co.saiman.measurement.fx;

import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.unitFormat;

import java.text.NumberFormat;

import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import javafx.util.StringConverter;
import uk.co.saiman.measurement.scalar.Scalar;

public interface QuantityFormatter {
  StringConverter<Number> quantityFormatter(Unit<?> unit);

  StringConverter<Number> quantityFormatter(Unit<?> unit, NumberFormat numberFormat);

  StringConverter<Unit<?>> unitFormatter();

  static QuantityFormatter defaultQuantityFormatter() {
    return new QuantityFormatter() {
      @Override
      public StringConverter<Number> quantityFormatter(Unit<?> unit) {
        return new StringConverter<Number>() {
          @Override
          public String toString(Number object) {
            return quantityFormat().format(new Scalar<>(unit, object));
          }

          @Override
          public Number fromString(String string) {
            Quantity<?> quantity = quantityFormat().parse(string);
            UnitConverter converter;
            try {
              converter = quantity.getUnit().getConverterToAny(unit);
            } catch (UnconvertibleException | IncommensurableException e) {
              throw new IllegalArgumentException(e);
            }
            return converter.convert(quantity.getValue());
          }
        };
      }

      @Override
      public StringConverter<Number> quantityFormatter(Unit<?> unit, NumberFormat numberFormat) {
        return new StringConverter<Number>() {
          @Override
          public String toString(Number object) {
            return quantityFormat()
                .withNumberFormat(numberFormat)
                .format(new Scalar<>(unit, object));
          }

          @Override
          public Number fromString(String string) {
            Quantity<?> quantity = quantityFormat().withNumberFormat(numberFormat).parse(string);
            UnitConverter converter;
            try {
              converter = quantity.getUnit().getConverterToAny(unit);
            } catch (UnconvertibleException | IncommensurableException e) {
              throw new IllegalArgumentException(e);
            }
            return converter.convert(quantity.getValue());
          }
        };
      }

      @Override
      public StringConverter<Unit<?>> unitFormatter() {
        return new StringConverter<Unit<?>>() {
          @Override
          public String toString(Unit<?> object) {
            return unitFormat().format(object);
          }

          @Override
          public Unit<?> fromString(String string) {
            return unitFormat().parse(string);
          }
        };
      }
    };
  }
}
