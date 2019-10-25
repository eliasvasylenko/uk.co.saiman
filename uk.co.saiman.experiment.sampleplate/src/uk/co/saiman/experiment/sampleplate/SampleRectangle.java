package uk.co.saiman.experiment.sampleplate;

import javax.measure.quantity.Length;

import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class SampleRectangle implements SampleArea {
  private final String id;
  private final XYCoordinate<Length> lowerBound;
  private final XYCoordinate<Length> upperBound;

  public SampleRectangle(
      String id,
      XYCoordinate<Length> lowerBound,
      XYCoordinate<Length> upperBound) {
    this.id = id;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public XYCoordinate<Length> center() {
    return lowerBound.add(upperBound).divide(2);
  }

  @Override
  public XYCoordinate<Length> lowerBound() {
    return lowerBound;
  }

  @Override
  public XYCoordinate<Length> upperBound() {
    return upperBound;
  }

  @Override
  public boolean isLocationReachable(XYCoordinate<Length> location) {
    var lowerInset = lowerBound.subtract(location);
    var upperInset = location.subtract(upperBound);
    return lowerInset.getXValue() > 0
        && lowerInset.getYValue() > 0
        && upperInset.getXValue() > 0
        && upperInset.getYValue() > 0;
  }
}
