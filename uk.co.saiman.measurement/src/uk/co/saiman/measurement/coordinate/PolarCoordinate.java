package uk.co.saiman.measurement.coordinate;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;

import uk.co.saiman.measurement.scalar.Scalar;

public class PolarCoordinate<R extends Quantity<R>> {
  private final Quantity<R> r;
  private final Quantity<Angle> theta;

  public PolarCoordinate(Quantity<R> r, Quantity<Angle> theta) {
    this.r = r;
    this.theta = theta;
  }

  public PolarCoordinate(Unit<R> unitR, Unit<Angle> unitTheta, double r, double theta) {
    this.r = new Scalar<>(unitR, r);
    this.theta = new Scalar<>(unitTheta, theta);
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
