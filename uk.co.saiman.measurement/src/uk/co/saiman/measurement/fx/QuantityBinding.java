package uk.co.saiman.measurement.fx;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.Binding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public interface QuantityBinding<T extends Quantity<T>> extends Binding<Quantity<T>> {
  QuantityBinding<T> convertTo(ObservableValue<? extends Unit<T>> unit);

  default QuantityBinding<T> convertTo(Unit<T> unit) {
    return convertTo(new SimpleObjectProperty<>(unit));
  }

  QuantityBinding<T> convertIntervalTo(ObservableValue<? extends Unit<T>> unit);

  default QuantityBinding<T> convertIntervalTo(Unit<T> unit) {
    return convertIntervalTo(new SimpleObjectProperty<>(unit));
  }

  DoubleBinding getDoubleAmount();

  Binding<Unit<T>> getUnit();
}
