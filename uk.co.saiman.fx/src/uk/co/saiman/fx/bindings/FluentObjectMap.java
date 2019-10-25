package uk.co.saiman.fx.bindings;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

public class FluentObjectMap<T, U> extends FluentObjectBinding<T> {
  private final ObservableValue<U> value;
  private final Function<U, T> mapping;

  public FluentObjectMap(ObservableValue<U> value, Function<U, T> mapping) {
    this.value = requireNonNull(value);
    this.mapping = requireNonNull(mapping);
    bind(value);
  }

  @Override
  protected T computeValue() {
    U fromValue = value.getValue();
    return fromValue == null ? null : mapping.apply(fromValue);
  }
}
