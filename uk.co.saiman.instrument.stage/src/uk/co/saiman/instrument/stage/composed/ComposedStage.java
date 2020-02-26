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

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.observable.ObservableProperty.over;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.axis.AxisDevice;
import uk.co.saiman.instrument.axis.AxisState;
import uk.co.saiman.instrument.sample.Analysis;
import uk.co.saiman.instrument.sample.Exchange;
import uk.co.saiman.instrument.sample.Failed;
import uk.co.saiman.instrument.sample.Ready;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.StageController;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public abstract class ComposedStage<T, U extends StageController<T>> extends DeviceImpl<U>
    implements Stage<T, U> {
  enum Mode {
    ANALYSING, EXCHANGING
  }

  private final List<AxisDevice<?, ?>> axes;
  private final T readyPosition;
  private final T exchangePosition;

  private final ObservableProperty<RequestedSampleState<T>> requestedSampleState;
  private final ObservableProperty<SampleState<T>> sampleState;

  private final ObservableProperty<T> actualPosition;

  private final List<Disposable> axisObservations;

  public ComposedStage(T analysisPosition, T exchangePosition, AxisDevice<?, ?>... axes) {
    this.axes = List.of(axes);
    this.readyPosition = analysisPosition;
    this.exchangePosition = exchangePosition;

    this.requestedSampleState = over(SampleState.ready());
    this.sampleState = over(SampleState.failed());
    this.actualPosition = ObservableProperty.over(NullPointerException::new);

    this.axisObservations = this.axes.stream().flatMap(this::observeAxis).collect(toList());

    updateState();
  }

  private Stream<Disposable> observeAxis(AxisDevice<?, ?> axis) {
    return Stream
        .of(
            axis.status().optionalValue().observe(s -> updateStatus()),
            axis.axisState().optionalValue().observe(s -> updateState()),
            axis.actualPosition().optionalValue().observe(p -> updateActualPosition()));
  }

  @Override
  protected void setDisposed() {
    super.setDisposed();
    axisObservations.forEach(Disposable::cancel);
  }

  private void updateStatus() {

  }

  private void updateState() {
    try {
      var axisStates = axes
          .stream()
          .map(AxisDevice::axisState)
          .map(ObservableValue::tryGet)
          .map(v -> v.orElse(AxisState.LOCATION_FAILED))
          .collect(toList());

      SampleState<T> sampleState;
      if (axisStates.stream().anyMatch(AxisState.LOCATION_FAILED::equals)) {
        sampleState = SampleState.failed();

      } else if (axisStates.stream().anyMatch(AxisState.LOCATION_REQUESTED::equals)) {
        sampleState = SampleState.transition();

      } else {
        sampleState = requestedSampleState.get();
      }

      this.sampleState.set(sampleState);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateActualPosition() {
    try {
      actualPosition.set(getActualPositionImpl());
    } catch (Exception e) {}
  }

  protected synchronized void requestSampleState(RequestedSampleState<T> requestedSampleState) {
    if (!this.requestedSampleState.isEqual(requestedSampleState)) {
      this.requestedSampleState.set(requestedSampleState);
      T requestedSamplePosition;
      if (requestedSampleState instanceof Analysis<?>) {
        requestedSamplePosition = ((Analysis<T>) requestedSampleState).position();
      } else if (requestedSampleState instanceof Exchange<?>) {
        requestedSamplePosition = exchangePosition;
      } else {
        requestedSamplePosition = readyPosition;
      }
      setRequestedStateImpl(requestedSampleState, requestedSamplePosition);
    }
  }

  protected SampleState<T> awaitRequest(long time, TimeUnit unit) {
    return sampleState()
        .value()
        .filter(requestedSampleState()::isEqual)
        .getNext()
        .orTimeout(time, unit)
        .join();
  }

  protected SampleState<T> awaitReady(long time, TimeUnit unit) {
    return sampleState()
        .value()
        .filter(s -> s instanceof Ready<?> || s instanceof Failed<?>)
        .getNext()
        .orTimeout(time, unit)
        .join();
  }

  @Override
  public ObservableValue<T> samplePosition() {
    return actualPosition;
  }

  @Override
  public ObservableValue<RequestedSampleState<T>> requestedSampleState() {
    return requestedSampleState;
  }

  @Override
  public ObservableValue<SampleState<T>> sampleState() {
    return sampleState;
  }

  protected abstract T getActualPositionImpl();

  protected abstract void setRequestedStateImpl(
      RequestedSampleState<T> requestedSampleState,
      T requestedSamplePosition);
}
