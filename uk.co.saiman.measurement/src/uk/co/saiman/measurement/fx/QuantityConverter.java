package uk.co.saiman.measurement.fx;

import static javafx.beans.binding.Bindings.createDoubleBinding;

import javax.measure.Quantity;
import javax.measure.Unit;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public interface QuantityConverter<T extends Quantity<T>> {
  default ValueConverter fromUnit(Unit<T> unit) {
    return fromUnit(new SimpleObjectProperty<>(unit));
  }

  ValueConverter fromUnit(ObservableValue<? extends Unit<T>> unit);

  default DoubleBinding convert(Quantity<T> value) {
    return convert(new SimpleObjectProperty<>(value));
  }

  default DoubleBinding convert(ObservableValue<? extends Quantity<T>> value) {
    return createDoubleBinding(
        () -> fromUnit(value.getValue().getUnit()).convert(value.getValue().getValue()).get(),
        value);
  }

  default DoubleBinding convertInterval(Quantity<T> value) {
    return convertInterval(new SimpleObjectProperty<>(value));
  }

  default DoubleBinding convertInterval(ObservableValue<? extends Quantity<T>> value) {
    return convert(value).subtract(convert(value.getValue().multiply(0)));
  }
}
