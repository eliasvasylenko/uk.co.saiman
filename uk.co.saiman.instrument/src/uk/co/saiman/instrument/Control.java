package uk.co.saiman.instrument;

public interface Control<T> extends AutoCloseable {
  @Override
  void close();

  boolean isClosed();

  T getController();
}
