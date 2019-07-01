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
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.saint.stage.impl;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.sample.Failed;
import uk.co.saiman.instrument.sample.Ready;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.saint.SaintProperties;
import uk.co.saiman.saint.stage.SampleArea;
import uk.co.saiman.saint.stage.SampleAreaStage;
import uk.co.saiman.saint.stage.SamplePlateStage;
import uk.co.saiman.saint.stage.SamplePlateStageController;

public class SamplePlateStageImpl extends DeviceImpl<SamplePlateStageController>
    implements SamplePlateStage {
  private final SaintStageManager stateManager;

  private final ObservableProperty<RequestedSampleState<SampleArea>> requestedSampleState;
  private final ObservableProperty<SampleState<SampleArea>> sampleState;

  private final ObservableProperty<SampleArea> actualPosition;

  public SamplePlateStageImpl(SaintProperties properties, SaintStageManager stateManager) {
    super(properties.samplePlateStageDeviceName().toString());

    this.stateManager = stateManager;

    this.sampleState = ObservableProperty.over(SampleState.ready());
    this.requestedSampleState = ObservableProperty.over(SampleState.ready());
    this.actualPosition = ObservableProperty.over(NullPointerException::new);
  }

  @Override
  public SampleAreaStage sampleAreaStage() {
    return stateManager.sampleAreaStage();
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return stateManager.xyStage().getLowerBound();
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return stateManager.xyStage().getUpperBound();
  }

  @Override
  public boolean isPositionReachable(SampleArea location) {
    return stateManager.xyStage().isPositionReachable(location.center().add(location.lowerBound()))
        && stateManager.xyStage().isPositionReachable(location.center().add(location.upperBound()));
  }

  @Override
  public ObservableValue<SampleState<SampleArea>> sampleState() {
    return sampleState;
  }

  @Override
  public ObservableValue<RequestedSampleState<SampleArea>> requestedSampleState() {
    return requestedSampleState;
  }

  @Override
  public ObservableValue<SampleArea> samplePosition() {
    return actualPosition;
  }

  synchronized void updateXySampleState(SampleState<XYCoordinate<Length>> xyState) {
    processStateMachine();
  }

  private void processStateMachine() {
    stateManager.processState();
  }

  private synchronized void validateRequestedArea(SampleArea location) {
    var offsetFromLowerBound = location
        .center()
        .add(location.lowerBound())
        .subtract(getLowerBound());
    var offsetFromUpperBound = location
        .center()
        .add(location.upperBound())
        .subtract(getUpperBound());

    if (offsetFromLowerBound.getX().getValue().doubleValue() < -0
        || offsetFromLowerBound.getY().getValue().doubleValue() < -0
        || offsetFromUpperBound.getX().getValue().doubleValue() > 0
        || offsetFromUpperBound.getY().getValue().doubleValue() > 0) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  protected SamplePlateStageController createController(ControlContext context) {
    context.run(() -> processStateMachine());
    return new SamplePlateStageController() {
      @Override
      public void close() {
        context.close();
      }

      @Override
      public boolean isClosed() {
        return context.isClosed();
      }

      @Override
      public void requestExchange() {
        context.run(() -> {
          requestedSampleState.set(SampleState.exchange());
          processStateMachine();
        });
      }

      @Override
      public void requestAnalysis(SampleArea position) {
        context.run(() -> {
          validateRequestedArea(position);
          requestedSampleState.set(SampleState.analysis(position));
          processStateMachine();
        });
      }

      @Override
      public void requestReady() {
        context.run(() -> {
          requestedSampleState.set(SampleState.ready());
          processStateMachine();
        });
      }

      @Override
      public SampleState<SampleArea> awaitRequest(long time, TimeUnit unit) {
        return context
            .get(
                () -> sampleState
                    .value()
                    .filter(s -> requestedSampleState().isMatching(r -> r.equals(s)))
                    .getNext()
                    .orTimeout(time, unit)
                    .join());
      }

      @Override
      public SampleState<SampleArea> awaitReady(long time, TimeUnit unit) {
        return context
            .get(
                () -> sampleState
                    .value()
                    .filter(s -> s instanceof Ready<?> || s instanceof Failed<?>)
                    .getNext()
                    .orTimeout(time, unit)
                    .join());
      }
    };
  }

  @Override
  protected void destroyController(ControlContext context) {
    processStateMachine();
    super.destroyController(context);
  }

  void requestReached() {
    sampleState.set(requestedSampleState.get());
  }

  public void requestFailed() {
    sampleState.set(SampleState.failed());
  }
}
