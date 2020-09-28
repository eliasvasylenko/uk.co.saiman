package uk.co.saiman.maldi.legacy.settings;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

public class DelayedExtraction {
  private final Quantity<Mass> focusMass;

  public DelayedExtraction(Quantity<Mass> focusMass) {
    this.focusMass = focusMass;
  }

  public Quantity<Mass> focusMass() {
    return focusMass;
  }
}
