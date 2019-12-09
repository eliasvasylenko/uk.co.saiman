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
 * This file is part of uk.co.saiman.experiment.sampleplate.
 *
 * uk.co.saiman.experiment.sampleplate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sampleplate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
