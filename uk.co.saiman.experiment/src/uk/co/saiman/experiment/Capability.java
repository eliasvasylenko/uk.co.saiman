package uk.co.saiman.experiment;

import java.util.Optional;

public abstract class Capability<T extends Resource> {
  // TODO sealed interface when language feature becomes available
  Capability() {}

  public abstract Optional<T> resolveResource(ExperimentStep<?> step);
}
