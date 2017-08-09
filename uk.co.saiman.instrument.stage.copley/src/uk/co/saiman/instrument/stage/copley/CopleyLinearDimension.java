/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.stage.copley.
 *
 * uk.co.saiman.instrument.stage.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.copley;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.comms.copley.MotorAxis;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.mathematics.Interval;

public class CopleyLinearDimension implements StageDimension<Length> {
  private final Units units;
  private final MotorAxis axis;
  private final CopleyController controller;

  public CopleyLinearDimension(Units units, MotorAxis axis, CopleyController controller) {
    this.units = units;
    this.axis = axis;
    this.controller = controller;
  }

  @Override
  public Unit<Length> getUnit() {
    return units.metre().get();
  }

  @Override
  public Interval<Quantity<Length>> getBounds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void requestPosition(Quantity<Length> offset) {
    int micrometreOffset = offset.to(units.metre().micro().get()).getValue().intValue();

    controller.getRequestedPosition().set(axis, new Int32(micrometreOffset));
  }

  @Override
  public Quantity<Length> getRequestedPosition() {
    return units.metre().micro().getQuantity(controller.getRequestedPosition().get(axis).value);
  }

  @Override
  public Quantity<Length> getActualPosition() {
    return units.metre().micro().getQuantity(controller.getActualPosition().get(axis).value);
  }
}
