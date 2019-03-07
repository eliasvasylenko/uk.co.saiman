package uk.co.saiman.experiment.product;

import uk.co.saiman.experiment.path.ProductPath;

/**
 * Represents a void product. There is no type of {@link Production production}
 * which produces {@link Nothing nothing}.
 * 
 * @author Elias N Vasylenko
 */
/*
 * TODO When project Amber is complete this should be an empty value type.
 */
public final class Nothing implements Product {
  private Nothing() {}

  @Override
  public ProductPath path() {
    throw new AssertionError();
  }
}
