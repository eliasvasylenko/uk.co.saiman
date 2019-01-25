package uk.co.saiman.experiment;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class Requirement<T extends Resource> {
  // TODO sealed interface when language feature becomes available
  Requirement() {}

  public abstract Optional<? extends Capability<T>> resolveCapability(Capability<?> capability);

  public abstract Stream<? extends Capability<T>> resolveCapabilities(Procedure<?> procedure);

  public Stream<T> resolveResources(ExperimentStep<?> step) {
    return resolveCapabilities(step.getProcedure()).flatMap(c -> c.resolveResource(step).stream());
  }
}
