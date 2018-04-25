package uk.co.saiman.instrument.stage.composed;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class ComposedXYStage extends ComposedStage<XYCoordinate<Length>> implements XYStage {
  private final StageAxis<Length> xAxis;
  private final StageAxis<Length> yAxis;

  private final XYCoordinate<Length> lowerBound;
  private final XYCoordinate<Length> upperBound;

  public ComposedXYStage(
      String name,
      Instrument instrument,
      StageAxis<Length> xAxis,
      StageAxis<Length> yAxis,
      XYCoordinate<Length> lowerBound,
      XYCoordinate<Length> upperBound,
      XYCoordinate<Length> analysisLocation,
      XYCoordinate<Length> exchangeLocation) {
    super(name, instrument, analysisLocation, exchangeLocation, xAxis, yAxis);

    this.xAxis = xAxis;
    this.yAxis = yAxis;

    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return lowerBound;
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return upperBound;
  }

  @Override
  public boolean isLocationReachable(XYCoordinate<Length> location) {
    return (getLowerBound().getX().subtract(location.getX()).getValue().doubleValue() <= 0)
        && (getLowerBound().getY().subtract(location.getY()).getValue().doubleValue() <= 0)
        && (getUpperBound().getX().subtract(location.getX()).getValue().doubleValue() >= 0)
        && (getUpperBound().getY().subtract(location.getY()).getValue().doubleValue() >= 0);
  }

  @Override
  protected XYCoordinate<Length> getActualLocationImpl() {
    return new XYCoordinate<>(xAxis.actualLocation().get(), yAxis.actualLocation().get());
  }

  @Override
  protected void setRequestedLocationImpl(XYCoordinate<Length> location) {
    xAxis.requestLocation(location.getX());
    yAxis.requestLocation(location.getY());
  }
}
