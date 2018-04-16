package uk.co.saiman.measurement.fx;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.Binding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public interface XYCoordinateBinding<T extends Quantity<T>>
    extends Binding<XYCoordinate<T>> {
  XYCoordinateBinding<T> convertTo(ObservableValue<? extends Unit<T>> unit);

  default XYCoordinateBinding<T> convertTo(Unit<T> unit) {
    return convertTo(new SimpleObjectProperty<>(unit));
  }

  XYCoordinateBinding<T> convertIntervalTo(ObservableValue<? extends Unit<T>> unit);

  default XYCoordinateBinding<T> convertIntervalTo(Unit<T> unit) {
    return convertIntervalTo(new SimpleObjectProperty<>(unit));
  }

  QuantityBinding<T> getX();

  QuantityBinding<T> getY();
}
