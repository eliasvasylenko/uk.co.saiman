package uk.co.saiman.measurement.fx;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public interface ValueConverter {
  default DoubleBinding convert(Number value) {
    return convert(new SimpleObjectProperty<>(value));
  }

  DoubleBinding convert(ObservableValue<? extends Number> value);

  default DoubleBinding convertInterval(Number value) {
    return convertInterval(new SimpleObjectProperty<>(value));
  }

  default DoubleBinding convertInterval(ObservableValue<? extends Number> value) {
    return convert(value).subtract(convert(0));
  }
}
