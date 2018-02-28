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
package uk.co.saiman.measurement.fx;

import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public final class QuantityBindings {
  private QuantityBindings() {}

  public static <T extends Quantity<T>> QuantityConverter<T> toUnit(Unit<T> unit) {
    return toUnit(new SimpleObjectProperty<>(unit));
  }

  public static <T extends Quantity<T>> QuantityConverter<T> toUnit(
      ObservableValue<? extends Unit<T>> unit) {
    return fromUnit -> {
      ObjectBinding<UnitConverter> converter = createObjectBinding(
          () -> unit.getValue().getConverterTo(unit.getValue()),
          unit);

      return value -> createDoubleBinding(
          () -> converter.getValue().convert(value.getValue()).doubleValue(),
          converter,
          value);
    };
  }
}
