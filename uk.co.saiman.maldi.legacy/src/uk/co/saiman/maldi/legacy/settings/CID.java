package uk.co.saiman.maldi.legacy.settings;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

public class CID {
  private final Quantity<Time> time;

  public CID(Quantity<Time> time) {
    this.time = time;
  }

  public Quantity<Time> time() {
    return time;
  }
}
