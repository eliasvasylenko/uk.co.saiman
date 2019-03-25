package uk.co.saiman.experiment.product;

import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;

public interface Product {
  ProductPath<Absolute> path();
}
