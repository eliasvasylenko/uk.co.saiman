package uk.co.saiman.comms;

public interface SimpleController<T> {
  T getController();

  void closeController();
}
