package uk.co.saiman.instrument.stage.composed;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class ComposedXYStage extends ComposedStage<XYCoordinate<Length>> implements XYStage {
  private final StageAxis<Length> xAxis;
  private final StageAxis<Length> yAxis;

  public ComposedXYStage(
      String name,
      Instrument instrument,
      StageAxis<Length> xAxis,
      StageAxis<Length> yAxis,
      XYCoordinate<Length> analysisLocation,
      XYCoordinate<Length> exchangeLocation) {
    super(name, instrument, analysisLocation, exchangeLocation);

    this.xAxis = xAxis;
    this.yAxis = yAxis;
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return new XYCoordinate<>(xAxis.getLowerBound(), yAxis.getLowerBound());
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return new XYCoordinate<>(xAxis.getUpperBound(), yAxis.getUpperBound());
  }

  @Override
  public boolean isLocationReachable(XYCoordinate<Length> position) {
    return (getLowerBound().getX().subtract(position.getX()).getValue().doubleValue() <= 0)
        && (getLowerBound().getY().subtract(position.getY()).getValue().doubleValue() <= 0)
        && (getUpperBound().getX().subtract(position.getX()).getValue().doubleValue() >= 0)
        && (getUpperBound().getY().subtract(position.getY()).getValue().doubleValue() >= 0);
  }

  @Override
  protected XYCoordinate<Length> getActualLocationForImpl() {
    return new XYCoordinate<>(xAxis.actualPosition().get(), yAxis.actualPosition().get());
  }

  @Override
  protected void setRequestedLocationImpl(XYCoordinate<Length> location) {
    xAxis.requestedPosition().set(location.getX());
    yAxis.requestedPosition().set(location.getY());
  }
}
