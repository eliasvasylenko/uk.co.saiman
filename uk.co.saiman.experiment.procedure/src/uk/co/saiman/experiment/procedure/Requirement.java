package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public abstract class Requirement<T extends Product> {
  public static NoRequirement none() {
    return NoRequirement.INSTANCE;
  }

  // TODO sealed interface when language feature becomes available
  Requirement() {}

  public boolean isIndependent() {
    return false;
  }

  public abstract Optional<? extends Production<? extends T>> resolveDependency(
      Production<?> capability);

  public abstract Stream<? extends Production<? extends T>> resolveDependencies(
      Conductor<?> procedure);

  public boolean resolvesDependency(Production<?> production) {
    return resolveDependency(production).isPresent();
  }
}
