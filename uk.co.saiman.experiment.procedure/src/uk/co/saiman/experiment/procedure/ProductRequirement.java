package uk.co.saiman.experiment.procedure;

import uk.co.saiman.experiment.product.Product;

public abstract class ProductRequirement<T extends Product> extends Requirement<T> {
  private final String id;

  ProductRequirement(String id) {
    this.id = id;
  }

  public String id() {
    return id;
  }
}
