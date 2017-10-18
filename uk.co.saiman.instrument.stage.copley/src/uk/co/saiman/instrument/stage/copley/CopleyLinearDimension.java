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

import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static uk.co.saiman.mathematics.Interval.bounded;
import static uk.co.saiman.observable.Observable.fixedRate;

import java.util.function.Supplier;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;

import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.mathematics.Interval;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

public class CopleyLinearDimension implements StageDimension<Length> {
  private final Units units;
  private final int axis;
  private final Supplier<CopleyController> controller;
  private Interval<Quantity<Length>> bounds;
  private final ObservableProperty<Quantity<Length>> requestedPosition;
  private final ObservableProperty<Quantity<Length>> actualPosition;

  public CopleyLinearDimension(
      int axis,
      Units units,
      Supplier<CopleyController> controller,
      Quantity<Length> minimum,
      Quantity<Length> maximum) {
    this.units = units;
    this.axis = axis;
    this.controller = controller;
    this.bounds = bounded(minimum, maximum, comparing(q -> q.getValue().doubleValue()));
    this.requestedPosition = new ObservablePropertyImpl<>(minimum);
    this.actualPosition = new ObservablePropertyImpl<>(minimum);

    this.requestedPosition.observe(this::positionRequested);
    fixedRate(0, 50, MILLISECONDS).observe(o -> updateActualPosition());
  }

  void positionRequested(Quantity<Length> position) {
    CopleyController controller = this.controller.get();
    if (controller != null)
      controller.getRequestedPosition().set(axis, new Int32(getStepsFromLength(position)));
  }

  void updateActualPosition() {
    CopleyController controller = this.controller.get();
    if (controller != null)
      actualPosition.set(getLengthFromSteps(controller.getActualPosition().get(axis).value));
  }

  @Override
  public Unit<Length> getUnit() {
    return units.metre().get();
  }

  @Override
  public Interval<Quantity<Length>> getBounds() {
    return bounds;
  }

  public int getStepsFromLength(Quantity<Length> length) {
    return length.to(units.metre().micro().get()).getValue().intValue();
  }

  public Quantity<Length> getLengthFromSteps(int steps) {
    return units.metre().micro().getQuantity(steps);
  }

  @Override
  public ObservableProperty<Quantity<Length>> requestedPosition() {
    return requestedPosition;
  }

  @Override
  public ObservableValue<Quantity<Length>> actualPosition() {
    return actualPosition;
  }
}
