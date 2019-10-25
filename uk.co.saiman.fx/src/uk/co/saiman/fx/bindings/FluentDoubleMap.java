package uk.co.saiman.fx.bindings;

import java.util.function.Function;

import javafx.beans.value.ObservableDoubleValue;

public class FluentDoubleMap extends FluentDoubleBinding {
  private final ObservableDoubleValue value;
  private final Function<Double, Double> mapping;

  public FluentDoubleMap(ObservableDoubleValue value, Function<Double, Double> mapping) {
    this.value = value;
    this.mapping = mapping;
    bind(value);
  }

  @Override
  protected double computeValue() {
    double fromValue = value.get();
    return mapping.apply(fromValue);
  }
}
