package uk.co.saiman.fx.bindings;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;

public class FluentObjectMapToDouble<U> extends FluentDoubleBinding {
  private final ObservableValue<U> value;
  private final Function<U, Double> mapping;

  public FluentObjectMapToDouble(FluentObjectBinding<U> value, Function<U, Double> mapping) {
    this.value = requireNonNull(value);
    this.mapping = requireNonNull(mapping);
    bind(value);
  }

  @Override
  protected double computeValue() {
    return mapping.apply(value.getValue());
  }
}
