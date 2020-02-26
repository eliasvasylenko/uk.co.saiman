package uk.co.saiman.locking;

public interface Lock extends AutoCloseable {
  @Override
  void close();
}
