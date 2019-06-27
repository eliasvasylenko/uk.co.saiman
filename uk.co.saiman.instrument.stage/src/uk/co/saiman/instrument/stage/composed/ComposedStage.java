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

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static uk.co.saiman.instrument.axis.AxisState.LOCATION_REACHED;
import static uk.co.saiman.observable.ObservableProperty.over;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;

import uk.co.saiman.instrument.Instrument;
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
  private final T readyPosition;
  private final T exchangePosition;

  private final ObservableProperty<RequestedSampleState<T>> requestedSampleState;
  private final ObservableProperty<SampleState<T>> sampleState;

  private final ObservableProperty<T> actualPosition;

  private final List<Disposable> axisObservations;

  public ComposedStage(
      String name,
      Instrument instrument,
      T analysisPosition,
      T exchangePosition,
      AxisDevice<?, ?>... axes) {
    super(name, instrument);
    this.axes = Set.of(axes);
    this.axes.forEach(axis -> addDependency(axis, 2, SECONDS));
    this.readyPosition = analysisPosition;
    this.exchangePosition = exchangePosition;

    this.requestedSampleState = over(SampleState.ready());
    this.sampleState = over(SampleState.failed());
    this.actualPosition = ObservableProperty.over(NullPointerException::new);

    this.axisObservations = concat(
        this.axes.stream().map(a -> a.axisState().value().observe(s -> updateState())),
        this.axes
            .stream()
            .map(a -> a.actualPosition().value().observe(p -> updateActualPosition())))
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

  private void updateActualPosition() {
    actualPosition.set(getActualPositionImpl());
  }

  private void updateSampleState(AxisState axisState) {
    SampleState<T> sampleState;

    switch (axisState) {
    case LOCATION_REACHED:
      sampleState = requestedSampleState.get();
      break;
    case LOCATION_REQUESTED:
      sampleState = SampleState.transition();
      break;
    default:
      sampleState = SampleState.failed();
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

  private void assertRegistered() {
    if (!getInstrumentRegistration().isRegistered()) {
      throw new IllegalStateException("Unable to request location when stage is unregistered");
    }
  }

  protected synchronized void requestSampleState(
      DependentControlContext context,
      RequestedSampleState<T> requestedSampleState) {
    context.run(() -> {
      if (!this.requestedSampleState.isEqual(requestedSampleState)) {
        assertRegistered();

        this.requestedSampleState.set(requestedSampleState);
        T requestedSamplePosition;
        if (requestedSampleState instanceof Analysis<?>) {
          requestedSamplePosition = ((Analysis<T>) requestedSampleState).position();
        } else if (requestedSampleState instanceof Exchange<?>) {
          requestedSamplePosition = exchangePosition;
        } else {
          requestedSamplePosition = readyPosition;
        }
        setRequestedStateImpl(context, requestedSampleState, requestedSamplePosition);
      }
    });
  }

  protected SampleState<T> awaitRequest(long time, TimeUnit unit) {
    return sampleState()
        .value()
        .filter(s -> requestedSampleState().isMatching(r -> r.equals(s)))
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
      DependentControlContext context,
      RequestedSampleState<T> requestedSampleState,
      T requestedSamplePosition);
}
