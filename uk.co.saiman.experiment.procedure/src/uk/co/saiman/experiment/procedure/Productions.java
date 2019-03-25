package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.product.Observation;
import uk.co.saiman.experiment.product.Preparation;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public class Productions {
  private Productions() {}

  public static boolean produces(Conductor<?> conductor, Production<?> production) {
    return conductor.products().anyMatch(production::equals);
  }

  /**
   * The observations which are made by experiment steps which follow this
   * procedure.
   * 
   * @return a stream of the observations which are prepared by the procedure
   */
  public static Stream<? extends Observation<?>> observations(Conductor<?> conductor) {
    return conductor
        .products()
        .filter(Observation.class::isInstance)
        .map(p -> (Observation<?>) p);
  }

  /**
   * The preparations which are made by experiment steps which follow this
   * procedure.
   * 
   * @return a stream of the preparations which are prepared by the procedure
   */
  public static Stream<Preparation<?>> preparations(Conductor<?> conductor) {
    return conductor
        .products()
        .filter(Preparation.class::isInstance)
        .map(p -> (Preparation<?>) p);
  }

  public static Optional<? extends Production<?>> production(Conductor<?> conductor, String id) {
    return conductor.products().filter(c -> c.id().equals(id)).findAny();
  }

  @SuppressWarnings("unchecked")
  public static <T extends Product> Optional<Conductor<? super T>> asDependent(
      Conductor<?> conductor,
      Production<T> production) {
    return conductor.directRequirement().resolveDependency(production).isPresent()
        ? Optional.of((Conductor<? super T>) conductor)
        : Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public static <S> Optional<Conductor<Nothing>> asIndependent(Conductor<?> conductor) {
    return conductor.directRequirement().isIndependent()
        ? Optional.of((Conductor<Nothing>) conductor)
        : Optional.empty();
  }
}
