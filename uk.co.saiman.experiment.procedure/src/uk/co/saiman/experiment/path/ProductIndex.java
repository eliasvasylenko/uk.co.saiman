package uk.co.saiman.experiment.path;

import uk.co.saiman.experiment.product.Product;

public interface ProductIndex {
  Product resolve(ProductPath path);

  <T extends Product> T resolve(Dependency<T> path);
}
