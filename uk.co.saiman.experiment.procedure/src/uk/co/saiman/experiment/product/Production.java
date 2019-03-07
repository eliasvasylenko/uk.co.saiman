package uk.co.saiman.experiment.product;

public abstract class Production<T extends Product> {
  // TODO sealed interface when language feature becomes available
  Production() {}

  public abstract String id();
}
