/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.instrument.stage.
 *
 * uk.co.saiman.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.composed;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public abstract class ComposedXYStage<T extends ComposedXYStageControl>
    extends ComposedStage<XYCoordinate<Length>, T> implements XYStage<T> {
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
