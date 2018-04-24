package uk.co.saiman.measurement.coordinate;

import static uk.co.saiman.measurement.Quantities.getQuantity;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;

public class PolarCoordinate<R extends Quantity<R>> {
  private final Quantity<R> r;
  private final Quantity<Angle> theta;

  public PolarCoordinate(Quantity<R> r, Quantity<Angle> theta) {
    this.r = r;
    this.theta = theta;
  }

  public PolarCoordinate(Unit<R> unitR, Unit<Angle> unitTheta, double r, double theta) {
    this.r = getQuantity(unitR, r);
    this.theta = getQuantity(unitTheta, theta);
  }

  public Quantity<R> getR() {
    return r;
  }

  public Quantity<Angle> getTheta() {
    return theta;
  }

  public Unit<R> getRUnit() {
    return r.getUnit();
  }

  public Unit<Angle> getThetaUnit() {
    return theta.getUnit();
  }

  public double getRValue() {
    return r.getValue().doubleValue();
  }

  public double getThetaValue() {
    return theta.getValue().doubleValue();
  }

  @Override
  public String toString() {
    return "(" + r + ", " + theta + ")";
  }
}
