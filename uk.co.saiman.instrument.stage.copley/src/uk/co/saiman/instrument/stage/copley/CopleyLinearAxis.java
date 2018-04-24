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

import java.util.function.Consumer;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.sample.SampleLocationUnknown;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.composed.StageAxis;
import uk.co.saiman.measurement.scalar.Scalar;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

public class CopleyLinearAxis implements StageAxis<Length> {
  private final CopleyController comms;
  private final int node;
  private final int axis;

  private final ObservableProperty<Quantity<Length>> actualLocation;

  public CopleyLinearAxis(CopleyController comms, int node, int axis) {
    this.comms = comms;
    this.node = node;
    this.axis = axis;

    this.actualLocation = new ObservablePropertyImpl<>(new SampleLocationUnknown());
    updateActualLocation();
    fixedRate(0, 50, MILLISECONDS).observe(o -> updateActualLocation());
  }

  protected void withAxis(Consumer<CopleyAxis> action, Runnable failure) {
    CopleyAxis axis = comms
        .getNodes()
        .filter(n -> n.getIndex() == node)
        .findFirst()
        .flatMap(n -> n.getAxes().filter(a -> a.getAxisNumber() == this.axis).findFirst())
        .orElse(null);

    if (axis != null) {
      action.accept(axis);
    } else {
      failure.run();
    }
  }

  void requestLocationImpl(Quantity<Length> location) {
    withAxis(
        axis -> axis.requestedPosition().set(new Int32(getStepsFromLength(location))),
        () -> {});
  }

  @Override
  public void requestLocation(Quantity<Length> location) {
    requestLocationImpl(location);
  }

  @Override
  public void abortRequest() {
    // TODO Auto-generated method stub

  }

  void updateActualLocation() {
    withAxis(
        axis -> actualLocation.set(getLengthFromSteps(axis.actualPosition().get().value)),
        () -> actualLocation.setProblem(new SampleLocationUnknown()));
  }

  public int getStepsFromLength(Quantity<Length> length) {
    return length.to(metre().micro().getUnit()).getValue().intValue();
  }

  public Quantity<Length> getLengthFromSteps(int steps) {
    return new Scalar<>(metre().micro(), steps);
  }

  @Override
  public ObservableValue<Quantity<Length>> actualLocation() {
    return actualLocation;
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
}
