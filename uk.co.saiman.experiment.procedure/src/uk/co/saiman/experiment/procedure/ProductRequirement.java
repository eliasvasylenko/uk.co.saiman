package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public abstract class ProductRequirement<T extends Product> extends Requirement<T> {
  private final Production<T> production;

  ProductRequirement(Production<T> production) {
    this.production = production;
  }

  public Production<T> production() {
    return production;
  }

  @Override
  public Optional<? extends Production<? extends T>> resolveDependency(Production<?> capability) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stream<? extends Production<? extends T>> resolveDependencies(Conductor<?> procedure) {
    // TODO Auto-generated method stub
    return null;
  }
}
