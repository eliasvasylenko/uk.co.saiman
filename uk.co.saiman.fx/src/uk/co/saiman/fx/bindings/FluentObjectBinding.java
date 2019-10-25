package uk.co.saiman.fx.bindings;

import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public abstract class FluentObjectBinding<T> extends ObjectBinding<T> {
  public static <T> FluentObjectBinding<T> over(ObservableValue<T> value) {
    return new FluentObjectBinding<T>() {
      {
        bind(value);
      }

      @Override
      protected T computeValue() {
        return value.getValue();
      }
    };
  }

  public FluentObjectBinding<T> orDefault(T defaultValue) {
    return or(new SimpleObjectProperty<>(defaultValue));
  }

  public FluentObjectBinding<T> or(ObservableValue<T> alternative) {
    return new FluentObjectOr<>(this, alternative);
  }

  public <U> FluentObjectBinding<U> map(Function<T, U> mapping) {
    return new FluentObjectMap<>(this, mapping);
  }

  public <U> FluentObjectBinding<U> flatMap(Function<T, ObservableValue<U>> mapping) {
    return new FluentObjectFlatMap<>(this, mapping);
  }

  public FluentDoubleBinding mapToDouble(Function<T, Double> mapping) {
    return new FluentObjectMapToDouble<>(this, mapping);
  }

  public FluentObjectBinding<T> filter(Predicate<T> filter) {
    return new FluentObjectFilter<>(this, filter);
  }

  public FluentObjectBinding<T> withDependency(ObservableValue<?>... o) {
    return new FluentObjectDependent<>(this, o);
  }

  public FluentObjectBinding<T> withNullableDependency(Observable o) {
    throw new UnsupportedOperationException();
  }
}
