package uk.co.saiman.maldi.legacy.settings;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

public class Gate {
  private final Quantity<Mass> mass;
  private final Quantity<Mass> width;

  public Gate(Quantity<Mass> mass, Quantity<Mass> width) {
    this.mass = mass;
    this.width = width;
  }

  public Quantity<Mass> mass() {
    return mass;
  }

  public Quantity<Mass> width() {
    return width;
  }
}
