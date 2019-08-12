package uk.co.saiman.experiment.dependency;

import java.util.function.Function;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.environment.ResourceClosedException;
import uk.co.saiman.experiment.environment.ResourceClosingException;

public interface Resource<T> extends Something, AutoCloseable {
  @Override
  Provision<T> source();

  T value();

  @Override
  void close();

  static <T extends AutoCloseable> Resource<T> over(Provision<T> provision, T value) {
    return over(provision, value, v -> v);
  }

  static <T> Resource<T> over(
      Provision<T> provision,
      T value,
      Function<? super T, ? extends AutoCloseable> close) {
    return new Resource<T>() {
      boolean closed = false;

      @Override
      public Provision<T> source() {
        return provision;
      }

      @Override
      public T value() {
        if (closed) {
          throw new ResourceClosedException(provision);
        }
        return value;
      }

      @Override
      public void close() {
        closed = true;
        try {
          close.apply(value).close();
        } catch (Exception e) {
          throw new ResourceClosingException(provision);
        }
      }
    };
  }
}
