package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.product.Production;

public class NoRequirement extends Requirement<Nothing> {
  NoRequirement() {}

  @Override
  public boolean isIndependent() {
    return true;
  }

  @Override
  public Optional<Production<Nothing>> resolveDependency(Production<?> capability) {
    return Optional.empty();
  }

  @Override
  public Stream<Production<Nothing>> resolveDependencies(Conductor<?, ?> procedure) {
    return Stream.empty();
  }
}
