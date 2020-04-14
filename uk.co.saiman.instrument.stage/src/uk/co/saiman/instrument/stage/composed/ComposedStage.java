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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import javax.measure.Quantity;

import uk.co.saiman.instrument.Controller;
import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.axis.AxisController;
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
    implements Stage<T> {
  enum Mode {
    ANALYSING, EXCHANGING
  }

  private final List<AxisDevice<?>> axes;
  private final Map<AxisDevice<?>, AxisController<?>> controllers;

  private final T readyPosition;
  private final T exchangePosition;

  private final ObservableProperty<Optional<RequestedSampleState<T>>> requestedSampleState;
  private RequestedSampleState<T> lastRequestedSampleState;
  private final ObservableProperty<SampleState<T>> sampleState;

  private final ObservableProperty<Optional<T>> actualPosition;

  private final List<Disposable> axisObservations;

  public ComposedStage(T analysisPosition, T exchangePosition, AxisDevice<?>... axes)
      throws InterruptedException, TimeoutException {
    this.axes = List.of(axes);
    this.controllers = new HashMap<>();
    for (var axis : this.axes) {
      this.controllers.put(axis, axis.acquireControl(100, TimeUnit.MILLISECONDS));
    }

    this.readyPosition = analysisPosition;
    this.exchangePosition = exchangePosition;

    this.requestedSampleState = ObservableProperty.over(Optional.empty());
    this.lastRequestedSampleState = null;
    this.sampleState = ObservableProperty.over(SampleState.failed());
    this.actualPosition = ObservableProperty.over(NullPointerException::new);

    this.axisObservations = this.axes.stream().flatMap(this::observeAxis).collect(toList());

    updateState();
  }

  @SuppressWarnings("unchecked")
  protected <V extends Quantity<V>> AxisController<V> getAxisController(AxisDevice<V> axis) {
    return (AxisController<V>) controllers.get(axis);
  }

  private Stream<Disposable> observeAxis(AxisDevice<?> axis) {
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
    controllers.values().forEach(Controller::close);
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
        sampleState = Optional.ofNullable(lastRequestedSampleState).orElse(SampleState.ready());
      }

      this.sampleState.set(sampleState);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateActualPosition() {
    try {
      actualPosition.set(Optional.of(getActualPositionImpl()));
    } catch (Exception e) {
      actualPosition.setProblem(() -> new IllegalStateException(e));
    }
  }

  protected synchronized void requestSampleState(RequestedSampleState<T> requestedSampleState) {
    if (!this.requestedSampleState.isValueEqual(Optional.of(requestedSampleState))) {
      this.requestedSampleState.set(Optional.of(requestedSampleState));
      this.lastRequestedSampleState = requestedSampleState;
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

  protected synchronized void withdrawRequest() {
    this.requestedSampleState.set(Optional.empty());
  }

  protected SampleState<T> awaitRequest(long time, TimeUnit unit) {
    return sampleState()
        .value()
        .filter(s -> requestedSampleState().isValueEqual(Optional.of(s)))
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
  public ObservableValue<Optional<T>> samplePosition() {
    return actualPosition;
  }

  @Override
  public ObservableValue<Optional<RequestedSampleState<T>>> requestedSampleState() {
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
