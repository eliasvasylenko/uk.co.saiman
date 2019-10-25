package uk.co.saiman.fx.bindings;

import java.util.Collection;
import java.util.Set;

import javafx.beans.value.ObservableValue;

public class FluentObjectDependent<T> extends FluentObjectBinding<T> {
  private final ObservableValue<T> value;
  private final Collection<ObservableValue<?>> dependencies;

  public FluentObjectDependent(ObservableValue<T> value, ObservableValue<?>... dependencies) {
    this.value = value;
    this.dependencies = Set.of(dependencies);
    bind(value);
    bind(dependencies);
  }

  @Override
  protected T computeValue() {
    if (dependencies.stream().anyMatch(d -> d.getValue() == null)) {
      return null;
    }
    return value.getValue();
  }
}
