/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.measurement.
 *
 * uk.co.saiman.measurement is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.measurement is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
