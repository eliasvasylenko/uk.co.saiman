package uk.co.saiman.experiment.environment.osgi;

import java.util.function.Function;

public final class ExclusiveResource<T> implements AutoCloseable {
  private final T value;
  private final Function<? super T, ? extends AutoCloseable> close;

  public ExclusiveResource(T value, Function<? super T, ? extends AutoCloseable> close) {
    this.value = value;
    this.close = close;
  }

  public T getValue() {
    return value;
  }

  @Override
  public void close() throws Exception {
    close.apply(value).close();
  }
}