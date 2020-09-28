package uk.co.saiman.maldi.legacy.settings;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

public class OperatingMassRange {
  private final Quantity<Mass> minimum;
  private final Quantity<Mass> maximum;

  public OperatingMassRange(Quantity<Mass> maximum, Quantity<Mass> minimum) {
    this.minimum = minimum;
    this.maximum = maximum;
  }

  Quantity<Mass> minimum() {
    return minimum;
  }

  Quantity<Mass> maximum() {
    return maximum;
  }
}
