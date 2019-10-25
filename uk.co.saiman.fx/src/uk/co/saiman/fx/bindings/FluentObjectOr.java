package uk.co.saiman.fx.bindings;

import static java.util.Objects.requireNonNull;

import javafx.beans.value.ObservableValue;

public class FluentObjectOr<T> extends FluentObjectBinding<T> {
  private final ObservableValue<? extends T> value;
  private final ObservableValue<? extends T> alternative;

  public FluentObjectOr(
      ObservableValue<? extends T> value,
      ObservableValue<? extends T> alternative) {
    this.value = requireNonNull(value);
    this.alternative = requireNonNull(alternative);
  }

  @Override
  protected synchronized T computeValue() {
    unbind(value, alternative);

    T fromValue = value.getValue();

    if (fromValue == null) {
      bind(value, alternative);
      return alternative.getValue();

    } else {
      bind(value);
      return fromValue;
    }
  }
}
