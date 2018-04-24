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

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.collections.FXCollections.observableList;
import static javafx.collections.FXCollections.unmodifiableObservableList;
import static uk.co.saiman.measurement.Quantities.getQuantity;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

/**
 * A utility class containing static methods to build conversion bindings.
 * 
 * @author Elias N Vasylenko
 */
public final class QuantityBindings {
  static class QuantityBindingImpl<T extends Quantity<T>> extends ObjectBinding<Quantity<T>>
      implements QuantityBinding<T> {
    private final ObservableValue<? extends Unit<T>> unit;
    private final ObservableValue<? extends Number> amount;

    public QuantityBindingImpl(
        ObservableValue<? extends Unit<T>> unit,
        ObservableValue<? extends Number> amount) {
      this.unit = unit;
      this.amount = amount;
      bind(unit, amount);
    }

    @Override
    protected Quantity<T> computeValue() {
      try {
        Unit<T> unit = this.unit.getValue();
        Number amount = this.amount.getValue();

        return unit != null && amount != null ? getQuantity(unit, amount) : null;
      } catch (Exception e) {
        return null;
      }
    }

    @Override
    public void dispose() {
      super.unbind(unit, amount);
    }

    @Override
    public ObservableList<?> getDependencies() {
      return unmodifiableObservableList(observableList(asList(unit, amount)));
    }

    @Override
    public QuantityBinding<T> convertTo(ObservableValue<? extends Unit<T>> unit) {
      return createQuantityBinding(unit, createObjectBinding(() -> {

        Unit<T> from = this.unit.getValue();
        Unit<T> to = unit.getValue();
        Number amount = this.amount.getValue();

        if (from != null && to != null && amount != null) {
          return from.getConverterTo(to).convert(amount);

        } else {
          return null;
        }

      }, this.unit, unit, amount));
    }

    @Override
    public QuantityBinding<T> convertIntervalTo(ObservableValue<? extends Unit<T>> unit) {
      return createQuantityBinding(unit, createObjectBinding(() -> {

        Unit<T> from = this.unit.getValue();
        Unit<T> to = unit.getValue();
        Number amount = this.amount.getValue();

        if (from != null && to != null && amount != null) {
          return from.getConverterTo(to).convert(amount);

        } else {
          return null;
        }

      }, this.unit, unit, amount));
    }

    @Override
    public DoubleBinding getDoubleAmount() {
      return Bindings
          .createDoubleBinding(
              () -> amount.getValue() == null ? 0 : amount.getValue().doubleValue(),
              amount);
    }

    @Override
    public Binding<Unit<T>> getUnit() {
      return Bindings
          .createObjectBinding(() -> unit.getValue() == null ? null : unit.getValue(), unit);
    }
  }

  private QuantityBindings() {}

  public static <T extends Quantity<T>> QuantityBinding<T> createQuantityBinding(
      ObservableValue<? extends Quantity<T>> quantity) {
    return new QuantityBindingImpl<>(
        createObjectBinding(
            () -> ofNullable(quantity.getValue()).map(Quantity::getUnit).orElse(null),
            quantity),
        createObjectBinding(
            () -> ofNullable(quantity.getValue()).map(Quantity::getValue).orElse(null),
            quantity));
  }

  public static <T extends Quantity<T>> QuantityBinding<T> createQuantityBinding(
      ObservableValue<? extends Unit<T>> unit,
      ObservableValue<? extends Number> amount) {
    return new QuantityBindingImpl<>(unit, amount);
  }

  public static <T extends Quantity<T>> QuantityBinding<T> createQuantityBinding(
      ObservableValue<? extends Unit<T>> unit,
      Number amount) {
    return createQuantityBinding(unit, new SimpleObjectProperty<>(amount));
  }

  public static <T extends Quantity<T>> QuantityBinding<T> createQuantityBinding(
      Unit<T> unit,
      ObservableValue<? extends Number> amount) {
    return createQuantityBinding(new SimpleObjectProperty<>(unit), amount);
  }

  public static <T extends Quantity<T>> QuantityBinding<T> createQuantityBinding(
      Unit<T> unit,
      Number amount) {
    return createQuantityBinding(
        new SimpleObjectProperty<>(unit),
        new SimpleObjectProperty<>(amount));
  }
}
