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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static uk.co.saiman.instrument.ConnectionState.DISCONNECTED;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_LOCATION_FAILED;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_LOCATION_REQUESTED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_FAILED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_REQUESTED;
import static uk.co.saiman.instrument.stage.composed.ComposedStage.Mode.ANALYSING;
import static uk.co.saiman.observable.ObservableProperty.over;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;

import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.DeviceRegistration;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.InstrumentRegistration;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public abstract class ComposedStage<T, U extends ComposedStageControl<T>> extends DeviceImpl<U>
    implements Stage<T, U> {
  enum Mode {
    ANALYSING, EXCHANGING
  }

  private final DeviceRegistration registration;
  private final Set<StageAxis<?>> axes;

  private final T analysisLocation;
  private final T exchangeLocation;
  private Mode mode;

  private final ObservableProperty<ConnectionState> connectionState;
  private final ObservableProperty<SampleState> sampleState;

  private final ObservableProperty<T> requestedLocation;
  private final ObservableProperty<T> actualLocation;

  private final List<Disposable> axisObservations;

  public ComposedStage(
      String name,
      Instrument instrument,
      T analysisLocation,
      T exchangeLocation,
      StageAxis<?>... axes) {
    super(name);
    this.axes = new HashSet<>(asList(axes));
    this.analysisLocation = analysisLocation;
    this.exchangeLocation = exchangeLocation;
    this.mode = ANALYSING;

    this.connectionState = over(DISCONNECTED);
    this.sampleState = over(ANALYSIS_LOCATION_FAILED);
    this.requestedLocation = ObservableProperty.over(exchangeLocation);
    this.requestedLocation.value().observe(l -> setRequestedLocation(ANALYSING, l));
    this.actualLocation = ObservableProperty.over(exchangeLocation);

    this.axisObservations = concat(
        this.axes.stream().map(a -> a.axisState().value().observe(s -> updateState())),
        this.axes
            .stream()
            .map(a -> a.actualLocation().value().observe(p -> updateActualLocation())))
                .collect(toList());

    this.registration = instrument.registerDevice(this);
  }

  @Override
  public InstrumentRegistration getInstrumentRegistration() {
    return registration.getInstrumentRegistration();
  }

  protected void dispose() {
    registration.deregister();
    axisObservations.forEach(Disposable::cancel);
  }

  private void updateState() {
    try {
      AxisState axisState = axes
          .stream()
          .map(StageAxis::axisState)
          .map(ObservableValue::get)
          .reduce(
              precedence(
                  AxisState.DISCONNECTED,
                  AxisState.LOCATION_REQUESTED,
                  AxisState.LOCATION_FAILED,
                  AxisState.LOCATION_REACHED))
          .orElse(AxisState.DISCONNECTED);

      updateConnectionState(axisState);
      updateSampleState(axisState);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void updateActualLocation() {
    actualLocation.set(getActualLocationImpl());
  }

  private void updateConnectionState(AxisState axisState) {
    ConnectionState connectionState = (axisState == AxisState.DISCONNECTED)
        ? ConnectionState.DISCONNECTED
        : ConnectionState.CONNECTED;

    this.connectionState.set(connectionState);
  }

  private void updateSampleState(AxisState axisState) {
    SampleState sampleState;

    switch (axisState) {
    case LOCATION_REACHED:
      sampleState = mode == Mode.ANALYSING ? ANALYSIS : EXCHANGE;
      break;
    case LOCATION_REQUESTED:
      sampleState = mode == Mode.ANALYSING ? ANALYSIS_LOCATION_REQUESTED : EXCHANGE_REQUESTED;
      break;
    default:
      sampleState = mode == Mode.ANALYSING ? ANALYSIS_LOCATION_FAILED : EXCHANGE_FAILED;
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

  synchronized SampleState requestExchange() {
    if (mode != Mode.EXCHANGING) {
      setRequestedLocation(Mode.EXCHANGING, exchangeLocation);

      return sampleState().value().filter(s -> EXCHANGE_REQUESTED != s).get();
    } else {
      return sampleState().get();
    }
  }

  synchronized SampleState requestAnalysis() {
    if (mode != Mode.ANALYSING) {
      setRequestedLocation(Mode.ANALYSING, analysisLocation);

      return sampleState().value().filter(s -> ANALYSIS_LOCATION_REQUESTED != s).get();
    } else {
      return sampleState().get();
    }
  }

  synchronized SampleState requestAnalysisLocation(T location) {
    if (!isLocationReachable(location)) {
      throw new IllegalArgumentException("Location unreachable " + location);
    }

    setRequestedLocation(Mode.ANALYSING, location);

    return sampleState().value().filter(s -> ANALYSIS_LOCATION_REQUESTED != s).get();
  }

  synchronized void setRequestedLocation(Mode mode, T location) {
    if (getInstrumentRegistration().isRegistered()) {
      this.mode = mode;
      setRequestedLocationImpl(location);
    }
  }

  @Override
  public ObservableValue<ConnectionState> connectionState() {
    return connectionState;
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

  @Override
  public void abortRequest() {
    axes.forEach(StageAxis::abortRequest);
  }

  protected abstract T getActualLocationImpl();

  protected abstract void setRequestedLocationImpl(T location);
}
