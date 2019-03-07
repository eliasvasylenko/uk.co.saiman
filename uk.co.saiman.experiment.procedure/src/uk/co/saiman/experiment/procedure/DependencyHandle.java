package uk.co.saiman.experiment.procedure;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.product.Product;

public interface DependencyHandle<T extends Product> {
  Requirement<T> requirement();

  Dependency<? extends T> dependency();

  boolean isValid();
}
