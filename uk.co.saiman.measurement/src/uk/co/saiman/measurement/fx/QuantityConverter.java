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

import static java.util.Objects.requireNonNull;
import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static uk.co.saiman.measurement.fx.QuantityBindings.getConverter;

import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public interface QuantityConverter<T extends Quantity<T>> {
  default ValueConverter fromUnit(Unit<T> unit) {
    requireNonNull(unit);
    return fromUnit(new SimpleObjectProperty<>(unit));
  }

  default ValueConverter fromUnit(ObservableValue<? extends Unit<T>> fromUnit) {
    ObjectBinding<Function<Number, Number>> converter = createObjectBinding(
        () -> getConverter(fromUnit.getValue(), getToUnit().getValue()),
        fromUnit,
        getToUnit());

    return value -> createDoubleBinding(
        () -> converter.getValue().apply(value.getValue()).doubleValue(),
        converter,
        value);
  }

  ObservableValue<? extends Unit<T>> getToUnit();

  default DoubleBinding convert(Quantity<T> value) {
    requireNonNull(value);
    return convert(new SimpleObjectProperty<>(value));
  }

  default DoubleBinding convert(ObservableValue<? extends Quantity<T>> fromValue) {
    requireNonNull(fromValue);
    return createDoubleBinding(
        () -> getConverter(fromValue.getValue().getUnit(), getToUnit().getValue())
            .apply(fromValue.getValue().getValue())
            .doubleValue(),
        fromValue,
        getToUnit());
  }

  default DoubleBinding convertInterval(Quantity<T> value) {
    requireNonNull(value);
    return convertInterval(new SimpleObjectProperty<>(value));
  }

  default DoubleBinding convertInterval(ObservableValue<? extends Quantity<T>> value) {
    requireNonNull(value);
    return convert(value).subtract(convert(value.getValue().multiply(0)));
  }
}
