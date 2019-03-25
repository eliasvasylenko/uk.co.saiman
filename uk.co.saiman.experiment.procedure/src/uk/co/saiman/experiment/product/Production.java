package uk.co.saiman.experiment.product;

import uk.co.saiman.experiment.procedure.Conductor;

public abstract class Production<T extends Product> {
  // TODO sealed interface when language feature becomes available
  Production() {}

  public boolean isPresent(Conductor<?> conductor) {
    return conductor.products().anyMatch(this::equals);
  }

  public abstract String id();
}
