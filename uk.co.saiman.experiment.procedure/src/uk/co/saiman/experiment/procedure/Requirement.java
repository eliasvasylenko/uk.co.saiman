package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public abstract class Requirement<T extends Product> {
  private static final NoRequirement NONE = new NoRequirement();

  public static NoRequirement none() {
    return NONE;
  }

  // TODO sealed interface when language feature becomes available
  Requirement() {}

  public boolean isIndependent() {
    return false;
  }

  public abstract Optional<? extends Production<? extends T>> resolveDependency(
      Production<?> capability);

  public abstract Stream<? extends Production<? extends T>> resolveDependencies(
      Conductor<?, ?> procedure);

  public boolean resolvesDependency(Production<?> production) {
    return resolveDependency(production).isPresent();
  }

  @SuppressWarnings("unchecked")
  public static <S, T extends Product> Optional<Conductor<S, ? super T>> asDependent(
      Conductor<S, ?> conductor,
      Production<T> production) {
    return conductor.requirement().resolveDependency(production).isPresent()
        ? Optional.of((Conductor<S, ? super T>) conductor)
        : Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public static <S> Optional<Conductor<S, Nothing>> asIndependent(Conductor<S, ?> conductor) {
    return conductor.requirement().isIndependent()
        ? Optional.of((Conductor<S, Nothing>) conductor)
        : Optional.empty();
  }
}
