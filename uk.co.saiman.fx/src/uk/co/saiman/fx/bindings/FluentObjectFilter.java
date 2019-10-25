package uk.co.saiman.fx.bindings;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

public class FluentObjectFilter<T> extends FluentObjectBinding<T> {
  private final ObservableValue<T> value;
  private final Predicate<T> filter;

  public FluentObjectFilter(ObservableValue<T> value, Predicate<T> filter) {
    this.value = requireNonNull(value);
    this.filter = requireNonNull(filter);
    bind(value);
  }

  @Override
  protected T computeValue() {
    T fromValue = value.getValue();
    return (fromValue == null || !filter.test(fromValue)) ? null : fromValue;
  }
}
