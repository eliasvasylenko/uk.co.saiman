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
import static java.util.stream.Stream.concat;
import static uk.co.saiman.instrument.axis.AxisState.LOCATION_REACHED;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_FAILED;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_REQUESTED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_FAILED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_REQUESTED;
import static uk.co.saiman.instrument.stage.composed.ComposedStage.Mode.ANALYSING;
import static uk.co.saiman.observable.ObservableProperty.over;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.axis.AxisDevice;
import uk.co.saiman.instrument.axis.AxisState;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.StageController;
import uk.co.saiman.instrument.virtual.AbstractingDevice;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public abstract class ComposedStage<T, U extends StageController<T>> extends AbstractingDevice<U>
    implements Stage<T, U> {
  enum Mode {
    ANALYSING, EXCHANGING
  }

  private final Set<AxisDevice<?, ?>> axes;

  private final T analysisLocation;
  private final T exchangeLocation;
  private Mode mode;

  private final ObservableProperty<SampleState> sampleState;

  private final ObservableProperty<T> requestedLocation;
  private final ObservableProperty<T> actualLocation;

  private final List<Disposable> axisObservations;

  public ComposedStage(
      String name,
      Instrument instrument,
      T analysisLocation,
      T exchangeLocation,
      AxisDevice<?, ?>... axes) {
    super(name, instrument);
    this.axes = Set.of(axes);
    this.axes.forEach(axis -> new DeviceDependency<>(axis, true));

    this.analysisLocation = analysisLocation;
    this.exchangeLocation = exchangeLocation;
    this.mode = ANALYSING;

    this.sampleState = over(ANALYSIS_FAILED);
    this.requestedLocation = ObservableProperty.over(exchangeLocation);
    this.actualLocation = ObservableProperty.over(exchangeLocation);

    this.axisObservations = concat(
        this.axes.stream().map(a -> a.axisState().value().observe(s -> updateState())),
        this.axes
            .stream()
            .map(a -> a.actualLocation().value().observe(p -> updateActualLocation())))
                .collect(toList());
  }

  @Override
  protected void dispose() {
    super.dispose();
    axisObservations.forEach(Disposable::cancel);
  }

  private void updateState() {
    try {
      AxisState axisState = axes
          .stream()
          .map(AxisDevice::axisState)
          .map(ObservableValue::tryGet)
          .map(v -> v.orElse(AxisState.LOCATION_FAILED))
          .reduce(
              precedence(
                  AxisState.LOCATION_REQUESTED,
                  AxisState.LOCATION_FAILED,
                  AxisState.LOCATION_REACHED))
          .orElse(LOCATION_REACHED);

      updateSampleState(axisState);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateActualLocation() {
    actualLocation.set(getActualLocationImpl());
  }

  private void updateSampleState(AxisState axisState) {
    SampleState sampleState;

    switch (axisState) {
    case LOCATION_REACHED:
      sampleState = mode == Mode.ANALYSING ? ANALYSIS : EXCHANGE;
      break;
    case LOCATION_REQUESTED:
      sampleState = mode == Mode.ANALYSING ? ANALYSIS_REQUESTED : EXCHANGE_REQUESTED;
      break;
    default:
      sampleState = mode == Mode.ANALYSING ? ANALYSIS_FAILED : EXCHANGE_FAILED;
      break;
    }

    this.sampleState.set(sampleState);
  }

  @SafeVarargs
  private final <V> BinaryOperator<V> precedence(V... precedence) {
    return (a, b) -> {
      for (V option : precedence)
        if (a == option || b == option)
          return option;
      return precedence[0];
    };
  }

  protected synchronized void requestExchange(AbstractingControlLock lock) {
    if (mode != Mode.EXCHANGING) {
      setRequestedLocation(lock, Mode.EXCHANGING, exchangeLocation);
    }
  }

  protected synchronized void requestAnalysis(AbstractingControlLock lock) {
    if (mode != Mode.ANALYSING) {
      setRequestedLocation(lock, Mode.ANALYSING, analysisLocation);
    }
  }

  protected synchronized void requestAnalysisLocation(AbstractingControlLock lock, T location) {
    if (!isLocationReachable(location)) {
      throw new IllegalArgumentException("Location unreachable " + location);
    }

    setRequestedLocation(lock, Mode.ANALYSING, location);
  }

  protected SampleState awaitRequest(long time, TimeUnit unit) {
    return sampleState()
        .value()
        .filter(s -> ANALYSIS_REQUESTED != s && EXCHANGE_REQUESTED != s)
        .getNext()
        .orTimeout(time, unit)
        .join();
  }

  protected SampleState awaitReady(long time, TimeUnit unit) {
    return sampleState()
        .value()
        .filter(s -> ANALYSIS_REQUESTED != s && EXCHANGE_REQUESTED != s && EXCHANGE != s)
        .getNext()
        .orTimeout(time, unit)
        .join();
  }

  private void assertRegistered() {
    if (!getInstrumentRegistration().isRegistered()) {
      throw new IllegalStateException("Unable to request location when stage is unregistered");
    }
  }

  protected synchronized void setRequestedLocation(
      AbstractingControlLock lock,
      Mode mode,
      T location) {
    assertRegistered();

    this.mode = mode;
    this.requestedLocation.set(location);
    setRequestedLocationImpl(lock, location);
  }

  @Override
  public ObservableValue<SampleState> sampleState() {
    return sampleState;
  }

  @Override
  public ObservableValue<T> requestedLocation() {
    return requestedLocation;
  }

  @Override
  public ObservableValue<T> actualLocation() {
    return actualLocation;
  }

  protected abstract T getActualLocationImpl();

  protected abstract void setRequestedLocationImpl(AbstractingControlLock lock, T location);
}
