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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static uk.co.saiman.measurement.Units.metre;
import static uk.co.saiman.observable.Observable.fixedRate;

import java.util.function.Supplier;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.composed.StageAxis;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

public class CopleyLinearDimension implements StageAxis<Length> {
  private final Supplier<CopleyAxis> axis;
  private final Quantity<Length> lowerBound;
  private final Quantity<Length> upperBound;
  private final ObservableProperty<Quantity<Length>> requestedPosition;
  private final ObservableProperty<Quantity<Length>> actualPosition;

  public CopleyLinearDimension(
      Supplier<CopleyAxis> axis,
      Quantity<Length> lowerBound,
      Quantity<Length> upperBound) {
    this.axis = axis;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.requestedPosition = new ObservablePropertyImpl<>(lowerBound);
    this.actualPosition = new ObservablePropertyImpl<>(lowerBound);

    this.requestedPosition.observe(this::positionRequested);
    fixedRate(0, 50, MILLISECONDS).observe(o -> updateActualPosition());
  }

  void positionRequested(Quantity<Length> position) {
    CopleyAxis axis = this.axis.get();
    if (axis != null)
      axis.requestedPosition().set(new Int32(getStepsFromLength(position)));
  }

  void updateActualPosition() {
    CopleyAxis axis = this.axis.get();
    if (axis != null)
      actualPosition.set(getLengthFromSteps(axis.actualPosition().get().value));
  }

  @Override
  public Quantity<Length> getLowerBound() {
    return lowerBound;
  }

  @Override
  public Quantity<Length> getUpperBound() {
    return upperBound;
  }

  public int getStepsFromLength(Quantity<Length> length) {
    return length.to(metre().micro().getUnit()).getValue().intValue();
  }

  public Quantity<Length> getLengthFromSteps(int steps) {
    return metre().micro().getQuantity(steps);
  }

  @Override
  public ObservableProperty<Quantity<Length>> requestedPosition() {
    return requestedPosition;
  }

  @Override
  public ObservableValue<Quantity<Length>> actualPosition() {
    return actualPosition;
  }

  @Override
  public ObservableValue<ConnectionState> connectionState() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ObservableValue<SampleState> sampleState() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void abortRequest() {
    // TODO Auto-generated method stub

  }
}
