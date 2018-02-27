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
