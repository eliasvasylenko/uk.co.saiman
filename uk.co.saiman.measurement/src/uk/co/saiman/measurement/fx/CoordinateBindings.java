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
import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.collections.FXCollections.observableList;
import static javafx.collections.FXCollections.unmodifiableObservableList;
import static uk.co.saiman.measurement.fx.QuantityBindings.createQuantityBinding;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

/**
 * A utility class containing static methods to build conversion bindings.
 * 
 * @author Elias N Vasylenko
 */
public final class CoordinateBindings {
  static class XYCoordinateBindingImpl<T extends Quantity<T>> extends ObjectBinding<XYCoordinate<T>>
      implements XYCoordinateBinding<T> {
    private final QuantityBinding<T> x;
    private final QuantityBinding<T> y;

    public XYCoordinateBindingImpl(
        ObservableValue<? extends Quantity<T>> x,
        ObservableValue<? extends Quantity<T>> y) {
      this.x = createQuantityBinding(x);
      this.y = createQuantityBinding(y);
      bind(x, y);
    }

    @Override
    protected XYCoordinate<T> computeValue() {
      try {
        return new XYCoordinate<>(x.getValue(), y.getValue());
      } catch (Exception e) {
        return null;
      }
    }

    @Override
    public void dispose() {
      super.unbind(x, y);
    }

    @Override
    public ObservableList<?> getDependencies() {
      return unmodifiableObservableList(observableList(asList(x, y)));
    }

    @Override
    public XYCoordinateBinding<T> convertTo(ObservableValue<? extends Unit<T>> unit) {
      return new XYCoordinateBindingImpl<>(x.convertTo(unit), y.convertTo(unit));
    }

    @Override
    public XYCoordinateBinding<T> convertIntervalTo(ObservableValue<? extends Unit<T>> unit) {
      return new XYCoordinateBindingImpl<>(x.convertIntervalTo(unit), y.convertIntervalTo(unit));
    }

    @Override
    public QuantityBinding<T> getX() {
      return x;
    }

    @Override
    public QuantityBinding<T> getY() {
      return y;
    }
  }

  private CoordinateBindings() {}

  public static <T extends Quantity<T>> XYCoordinateBinding<T> createCoordinateBinding(
      ObservableValue<? extends XYCoordinate<T>> coordinate) {
    return new XYCoordinateBindingImpl<>(
        createObjectBinding(() -> coordinate.getValue().getX(), coordinate),
        createObjectBinding(() -> coordinate.getValue().getY(), coordinate));
  }

  public static <T extends Quantity<T>> XYCoordinateBinding<T> createCoordinateBinding(
      ObservableValue<? extends Quantity<T>> x,
      ObservableValue<? extends Quantity<T>> y) {
    return new XYCoordinateBindingImpl<>(x, y);
  }

  public static <T extends Quantity<T>> XYCoordinateBinding<T> createCoordinateBinding(
      Quantity<T> x,
      ObservableValue<? extends Quantity<T>> y) {
    return new XYCoordinateBindingImpl<>(new SimpleObjectProperty<>(x), y);
  }

  public static <T extends Quantity<T>> XYCoordinateBinding<T> createCoordinateBinding(
      ObservableValue<? extends Quantity<T>> x,
      Quantity<T> y) {
    return new XYCoordinateBindingImpl<>(x, new SimpleObjectProperty<>(y));
  }

  public static <T extends Quantity<T>> XYCoordinateBinding<T> createCoordinateBinding(
      Quantity<T> x,
      Quantity<T> y) {
    return new XYCoordinateBindingImpl<>(
        new SimpleObjectProperty<>(x),
        new SimpleObjectProperty<>(y));
  }

}
