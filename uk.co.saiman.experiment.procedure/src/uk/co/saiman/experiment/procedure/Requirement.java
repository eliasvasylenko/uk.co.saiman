package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.procedure.ResultRequirement.Cardinality;
import uk.co.saiman.experiment.product.Observation;
import uk.co.saiman.experiment.product.Preparation;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public abstract class Requirement<T extends Product> {
  public static NoRequirement none() {
    return NoRequirement.INSTANCE;
  }

  public static <T> ResultRequirement<T> on(Observation<T> observation) {
    return new ResultRequirement<>(observation);
  }

  public static <T> ResultRequirement<T> on(Observation<T> observation, Cardinality cardinality) {
    return new ResultRequirement<>(observation, cardinality);
  }

  public static <T> ConditionRequirement<T> on(Preparation<T> preparation) {
    return new ConditionRequirement<>(preparation);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Product> ProductRequirement<T> on(Production<?> production) {
    return production instanceof Observation<?>
        ? (ProductRequirement<T>) on((Observation<?>) production)
        : (ProductRequirement<T>) on((Preparation<?>) production);
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
