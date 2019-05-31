package uk.co.saiman.instrument;

public interface Controller extends AutoCloseable {
  @Override
  default void close() {}
}
