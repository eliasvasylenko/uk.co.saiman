/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.sampleplate.
 *
 * uk.co.saiman.instrument.sampleplate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.sampleplate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
  public XYCoordinate<Length> rest() {
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
    return lowerInset.getXValue() > 0 && lowerInset.getYValue() > 0 && upperInset.getXValue() > 0
        && upperInset.getYValue() > 0;
  }
}
