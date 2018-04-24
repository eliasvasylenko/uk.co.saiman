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
import static uk.co.saiman.instrument.ConnectionState.CONNECTED;
import static uk.co.saiman.instrument.ConnectionState.DISCONNECTED;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_LOCATION_FAILED;
import static uk.co.saiman.instrument.sample.SampleState.ANALYSIS_LOCATION_REQUESTED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_FAILED;
import static uk.co.saiman.instrument.sample.SampleState.EXCHANGE_REQUESTED;
import static uk.co.saiman.observable.ObservableProperty.over;

import java.util.HashSet;
import java.util.Set;

import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public abstract class ComposedStage<T> implements Stage<T> {
  private final String name;
  private final Instrument instrument;
  private final Set<StageAxis<?>> axes;

  private final ObservableProperty<ConnectionState> connectionState;
  private final ObservableProperty<SampleState> sampleState;

  private final ObservableProperty<T> requestedLocation;
  private final ObservableProperty<T> actualLocation;

  private final T analysisLocation;
  private boolean analysisRequested;
  private final T exchangeLocation;
  private boolean exchangeRequested;

  public ComposedStage(
      String name,
      Instrument instrument,
      T analysisLocation,
      T exchangeLocation,
      StageAxis<?>... axes) {
    this.name = name;
    this.instrument = instrument;
    this.axes = new HashSet<>(asList(axes));

    this.connectionState = over(DISCONNECTED);
    this.axes.forEach(a -> a.connectionState().observe(s -> updateConnectionState()));

    this.sampleState = over(ANALYSIS_LOCATION_FAILED);
    this.axes.forEach(a -> a.sampleState().observe(s -> updateSampleState()));

    this.analysisLocation = analysisLocation;
    this.exchangeLocation = exchangeLocation;
    requestedLocation = over(exchangeLocation);
    requestedLocation.observe(this::setRequestedLocationImpl);
    actualLocation = over(exchangeLocation);
    this.axes
        .forEach(
            a -> a.actualLocation().observe(p -> actualLocation.set(getActualLocationImpl())));

    instrument.addDevice(this);
  }

  private void updateConnectionState() {
    ConnectionState connectionState = axes
        .stream()
        .map(StageAxis::connectionState)
        .map(ObservableValue::get)
        .reduce((a, b) -> precedence(a, b, DISCONNECTED, CONNECTED))
        .orElse(DISCONNECTED);

    this.connectionState.set(connectionState);
  }

  private void updateSampleState() {
    SampleState sampleState = axes
        .stream()
        .map(StageAxis::sampleState)
        .map(ObservableValue::get)
        .reduce(
            (a, b) -> precedence(
                a,
                b,
                ANALYSIS_LOCATION_REQUESTED,
                EXCHANGE_REQUESTED,
                ANALYSIS_LOCATION_FAILED,
                EXCHANGE_FAILED,
                ANALYSIS,
                EXCHANGE))
        .orElse(ANALYSIS_LOCATION_REQUESTED);

    this.sampleState.set(sampleState);
  }

  @SafeVarargs
  private final <U> U precedence(U a, U b, U... precedence) {
    for (U option : precedence)
      if (a == option || b == option)
        return option;
    return precedence[0];
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Instrument getInstrument() {
    return instrument;
  }

  @Override
  public SampleState requestExchange() {
    if (!exchangeRequested) {
      setRequestedLocationImpl(exchangeLocation);

      exchangeRequested = true;
      analysisRequested = false;

      return sampleState().filter(s -> EXCHANGE_REQUESTED != s).get();
    } else {
      return sampleState().get();
    }
  }

  @Override
  public SampleState requestAnalysis() {
    if (!analysisRequested) {
      setRequestedLocationImpl(analysisLocation);

      analysisRequested = true;
      exchangeRequested = false;

      return sampleState().filter(s -> ANALYSIS_LOCATION_REQUESTED != s).get();
    } else {
      return sampleState().get();
    }
  }

  @Override
  public synchronized SampleState requestLocation(T location) {
    if (!isLocationReachable(location)) {
      throw new IllegalArgumentException("Location unreachable " + location);
    }
    sampleState.set(ANALYSIS_LOCATION_REQUESTED);
    setRequestedLocationImpl(location);

    return sampleState().filter(s -> ANALYSIS_LOCATION_REQUESTED != s).get();
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
