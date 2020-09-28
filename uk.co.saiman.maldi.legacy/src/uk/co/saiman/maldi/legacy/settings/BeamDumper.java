package uk.co.saiman.maldi.legacy.settings;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

public class BeamDumper {
  private final Quantity<Mass> mass;

  public BeamDumper(Quantity<Mass> mass) {
    this.mass = mass;
  }

  public Quantity<Mass> mass() {
    return mass;
  }
}
