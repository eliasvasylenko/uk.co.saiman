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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.impl;

import static uk.co.saiman.measurement.Units.metre;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.sample.Analysis;
import uk.co.saiman.instrument.sample.Failed;
import uk.co.saiman.instrument.sample.Ready;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.maldi.stage.SampleArea;
import uk.co.saiman.maldi.stage.SampleAreaStage;
import uk.co.saiman.maldi.stage.SampleAreaStageController;
import uk.co.saiman.maldi.stage.SamplePlateStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

/**
 * An implementation of a stage for the MALDI instrument which is backed by an
 * {@link XYStage} implementation. The backing stage must be rectangular, with
 * an accessible area over the MALDI sample plate.
 * 
 * @author Elias N Vasylenko
 *
 */
class SampleAreaStageImpl extends DeviceImpl<SampleAreaStageController> implements SampleAreaStage {
  private final MaldiStageManager stateManager;

  private static final XYCoordinate<Length> ZERO = new XYCoordinate<>(metre().getUnit(), 0, 0);

  private final ObservableProperty<RequestedSampleState<XYCoordinate<Length>>> requestedSampleState;
  private final ObservableProperty<SampleState<XYCoordinate<Length>>> sampleState;

  private final ObservableProperty<XYCoordinate<Length>> actualPosition;

  public SampleAreaStageImpl(MaldiStageManager stateManager) {
    this.stateManager = stateManager;

    this.sampleState = ObservableProperty.over(SampleState.exchange());
    this.requestedSampleState = ObservableProperty.over(SampleState.exchange());
    this.actualPosition = ObservableProperty.over(NullPointerException::new);
  }

  @Override
  public SamplePlateStage samplePlateStage() {
    return stateManager.samplePlateStage();
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return samplePlateStage()
        .requestedSampleState()
        .tryGet()
        .filter(r -> r instanceof Analysis<?>)
        .map(r -> ((Analysis<SampleArea>) r).position().lowerBound())
        .orElse(ZERO);
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return samplePlateStage()
        .requestedSampleState()
        .tryGet()
        .filter(r -> r instanceof Analysis<?>)
        .map(r -> ((Analysis<SampleArea>) r).position().upperBound())
        .orElse(ZERO);
  }

  @Override
  public boolean isPositionReachable(XYCoordinate<Length> location) {
    return samplePlateStage()
        .requestedSampleState()
        .tryGet()
        .filter(r -> r instanceof Analysis<?>)
        .map(r -> ((Analysis<SampleArea>) r).position().isLocationReachable(location))
        .orElse(false);
  }

  @Override
  public ObservableValue<SampleState<XYCoordinate<Length>>> sampleState() {
    return sampleState;
  }

  @Override
  public ObservableValue<RequestedSampleState<XYCoordinate<Length>>> requestedSampleState() {
    return requestedSampleState;
  }

  @Override
  public ObservableValue<XYCoordinate<Length>> samplePosition() {
    return actualPosition;
  }

  private void processStateMachine() {
    stateManager.processState();
  }

  private synchronized void validateRequestedOffset(XYCoordinate<Length> offset) {
    var offsetFromLowerBound = samplePlateStage()
        .requestedSampleState()
        .tryGet()
        .filter(r -> r instanceof Analysis<?>)
        .map(r -> ((Analysis<SampleArea>) r).position().lowerBound())
        .map(lowerBound -> offset.subtract(lowerBound))
        .orElse(offset);

    var offsetFromUpperBound = samplePlateStage()
        .requestedSampleState()
        .tryGet()
        .filter(r -> r instanceof Analysis<?>)
        .map(r -> ((Analysis<SampleArea>) r).position().upperBound())
        .map(upperBound -> offset.subtract(upperBound))
        .orElse(offset);

    if (offsetFromLowerBound.getX().getValue().doubleValue() < 0
        || offsetFromLowerBound.getY().getValue().doubleValue() < 0
        || offsetFromUpperBound.getX().getValue().doubleValue() > 0
        || offsetFromUpperBound.getY().getValue().doubleValue() > 0) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  protected SampleAreaStageController createController(ControlContext context) {
    context.run(() -> processStateMachine());
    return new SampleAreaStageController() {
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
      public void requestReady() {
        context.run(() -> {
          requestedSampleState.set(SampleState.ready());
          processStateMachine();
        });
      }

      @Override
      public void requestAnalysis(XYCoordinate<Length> position) {
        context.run(() -> {
          validateRequestedOffset(position);
          requestedSampleState.set(SampleState.analysis(position));
          processStateMachine();
        });
      }

      @Override
      public SampleState<XYCoordinate<Length>> awaitRequest(long time, TimeUnit unit) {
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
      public SampleState<XYCoordinate<Length>> awaitReady(long time, TimeUnit unit) {
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
