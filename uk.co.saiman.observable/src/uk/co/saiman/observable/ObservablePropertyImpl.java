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
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.observable;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * A simple implementation of {@link ObservableProperty} which maintains a list
 * of listeners to receive change events fired with {@link #set(Object)}.
 * <p>
 * Addition and removal of observers, as well as the firing of events, are
 * synchronized on the implementation object.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of event message to produce
 */
public class ObservablePropertyImpl<T> implements ObservableProperty<T> {
  private final HotObservable<T> backingObservable;

  private T value;
  private Throwable failure;

  public ObservablePropertyImpl(T initialValue) {
    this.backingObservable = new HotObservable<>();
    this.value = requireNonNull(initialValue);
  }

  public ObservablePropertyImpl(Throwable initialProblem) {
    this.backingObservable = new HotObservable<>();
    this.failure = requireNonNull(initialProblem);
  }

  @Override
  public Observable<Change<T>> changes() {
    return observer -> backingObservable
        .materialize()
        .repeating()
        .observe(new PassthroughObserver<Observable<T>, Change<T>>(observer) {
          private ObservableValue<T> previousValue;

          @Override
          public void onObserve(Observation observation) {
            this.previousValue = currentState();
            super.onObserve(observation);
          }

          @Override
          public void onNext(Observable<T> message) {
            ObservableValue<T> previousValue = this.previousValue;
            ObservableValue<T> newValue = message.toValue();
            this.previousValue = newValue;

            getDownstreamObserver().onNext(new Change<T>() {
              @Override
              public ObservableValue<T> previousValue() {
                return previousValue;
              }

              @Override
              public ObservableValue<T> newValue() {
                return newValue;
              }
            });
          }
        });
  }

  public ObservableValue<T> currentState() {
    if (value != null) {
      return Observable.value(value);
    } else {
      return Observable.failingValue(failure);
    }
  }

  @Override
  public synchronized Disposable observe(Observer<? super T> observer) {
    ObservationImpl<T> disposable = backingObservable.observeImpl(observer);

    if (value != null) {
      disposable.onNext(value);
    } else {
      disposable.onFail(failure);
    }

    return disposable;
  }

  @Override
  public synchronized T set(T value) {
    if (failure == null && Objects.equals(this.value, value))
      return value;

    T previous = this.value;
    failure = null;
    this.value = value;

    backingObservable.next(value);

    return previous;
  }

  @Override
  public synchronized void setProblem(Throwable t) {
    value = null;
    failure = t;

    backingObservable.fail(t);

    backingObservable.start();
  }

  @Override
  public synchronized T get() {
    if (value == null)
      throw new MissingValueException(this, failure);
    return value;
  }
}
