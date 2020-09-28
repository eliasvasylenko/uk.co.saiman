package uk.co.saiman.maldi.legacy.settings;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

public class LaserPowerOptimisation {
  public enum Guidance {
    CONSERVATIVE, LIBERAL, MANUAL
  }

  private Quantity<Dimensionless> laserThresholdAtSignalLow;
  private Quantity<Dimensionless> laserThresholdAtSignalHigh;
  private Quantity<Dimensionless> laserThresholdMaximum;
  private Quantity<Dimensionless> laserPowerIncrement;
}
