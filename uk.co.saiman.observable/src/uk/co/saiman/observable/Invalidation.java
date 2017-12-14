package uk.co.saiman.observable;

import java.util.function.Function;

public interface Invalidation<T> {
  T revalidate();

  default <U> Invalidation<U> map(Function<T, U> mapping) {
    return () -> mapping.apply(revalidate());
  }
}
