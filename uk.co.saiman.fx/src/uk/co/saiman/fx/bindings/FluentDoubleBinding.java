package uk.co.saiman.fx.bindings;

import java.util.function.Function;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;

public abstract class FluentDoubleBinding extends DoubleBinding {
  public static FluentDoubleBinding over(ObservableValue<Number> value) {
    return new FluentDoubleBinding() {
      {
        bind(value);
      }

      @Override
      protected double computeValue() {
        return value.getValue().doubleValue();
      }
    };
  }

  public FluentDoubleBinding map(Function<Double, Double> mapping) {
    return new FluentDoubleMap(this, mapping);
  }

  public <U> FluentObjectBinding<U> mapToObject(Function<Double, U> mapping) {
    return new FluentDoubleMapToObject<>(this, mapping);
  }
}
