package uk.co.saiman.experiment.product;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Instruction;

/**
 * A production is simply a representation of an API point. In particular, it
 * represents an artifact which can be produced as a result of {@link Conductor
 * conducting} an {@link Instruction instruction} of an experiment procedure,
 * and the Java type which the product may be materialized as.
 * <p>
 * Production instances are intended to be static, and do not prescribe the
 * method of collecting or storing the product data. The data should be stored
 * according to a {@link DataFormat format} which is compatible with the type of
 * the product.
 */
public abstract class Production<T extends Product> {
  // TODO sealed interface when language feature becomes available
  Production() {}

  public boolean isPresent(Conductor<?> conductor) {
    return conductor.products().anyMatch(this::equals);
  }

  public abstract String id();
}
