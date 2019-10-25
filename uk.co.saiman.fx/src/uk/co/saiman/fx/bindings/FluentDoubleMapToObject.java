package uk.co.saiman.fx.bindings;

import java.util.function.Function;

import javafx.beans.value.ObservableDoubleValue;

public class FluentDoubleMapToObject<T> extends FluentObjectBinding<T> {
  private final ObservableDoubleValue value;
  private final Function<Double, T> mapping;

  public FluentDoubleMapToObject(ObservableDoubleValue value, Function<Double, T> mapping) {
    this.value = value;
    this.mapping = mapping;
    bind(value);
  }

  @Override
  protected T computeValue() {
    double fromValue = value.get();
    return mapping.apply(fromValue);
  }
}
