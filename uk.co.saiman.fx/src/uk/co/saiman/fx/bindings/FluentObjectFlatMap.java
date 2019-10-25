package uk.co.saiman.fx.bindings;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;

public class FluentObjectFlatMap<T, U> extends FluentObjectBinding<T> {
  private final ObservableValue<U> value;
  private final Function<U, ObservableValue<T>> mapping;

  private ObservableValue<T> mappedValue;

  public FluentObjectFlatMap(ObservableValue<U> value, Function<U, ObservableValue<T>> mapping) {
    this.value = requireNonNull(value);
    this.mapping = requireNonNull(mapping);
  }

  @Override
  protected synchronized T computeValue() {
    if (mappedValue != null) {
      unbind(value, mappedValue);
    } else {
      unbind(value);
    }

    U fromValue = value.getValue();

    mappedValue = fromValue == null ? null : mapping.apply(fromValue);
    if (mappedValue != null) {
      bind(value, mappedValue);
      return mappedValue.getValue();

    } else {
      bind(value);
      return null;
    }
  }
}
