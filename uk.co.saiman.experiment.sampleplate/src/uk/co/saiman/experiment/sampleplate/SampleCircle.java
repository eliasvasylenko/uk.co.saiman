package uk.co.saiman.experiment.sampleplate;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class SampleCircle implements SampleArea {
  private final String id;
  private final XYCoordinate<Length> center;
  private final Quantity<Length> radius;

  public SampleCircle(String id, XYCoordinate<Length> center, Quantity<Length> radius) {
    this.id = id;
    this.center = center;
    this.radius = radius;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public XYCoordinate<Length> center() {
    return center;
  }

  public Quantity<Length> radius() {
    return radius;
  }

  @Override
  public XYCoordinate<Length> lowerBound() {
    return center.subtract(new XYCoordinate<>(radius, radius));
  }

  @Override
  public XYCoordinate<Length> upperBound() {
    return center.add(new XYCoordinate<>(radius, radius));
  }

  @Override
  public boolean isLocationReachable(XYCoordinate<Length> location) {
    var radius = this.radius.getValue().doubleValue();
    var distance = location.getLength().to(this.radius.getUnit()).getValue().doubleValue();
    return radius >= distance;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + id + ", " + center + ", " + radius + ")";
  }
}
