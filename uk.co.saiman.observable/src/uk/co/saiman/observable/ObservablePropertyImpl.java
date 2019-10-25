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
import java.util.Optional;
import java.util.function.Supplier;

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
  private static class Message<T> {
    private final T value;
    private final Supplier<Throwable> failure;

    public Message(T value) {
      this.value = Objects.requireNonNull(value);
      this.failure = null;
    }

    public Message(Supplier<Throwable> failure) {
      this.value = null;
      this.failure = Objects.requireNonNull(failure);
    }

    public Optional<Supplier<Throwable>> failure() {
      if (value != null) {
        return Optional.empty();
      } else {
        return Optional.of(failure);
      }
    }

    public Optional<T> value() {
      return Optional.ofNullable(value);
    }

    public ObservableValue<T> materialize() {
      if (value == null) {
        return ObservableValue.empty(failure);
      } else {
        return ObservableValue.of(value);
      }
    }
  }

  private final HotObservable<Message<T>> backingObservable;

  private T value;
  private Supplier<Throwable> failure;

  public ObservablePropertyImpl(T initialValue) {
    this.backingObservable = new HotObservable<>();
    this.value = requireNonNull(initialValue);
  }

  public ObservablePropertyImpl() {
    this(NullPointerException::new);
  }

  public ObservablePropertyImpl(Supplier<Throwable> initialProblem) {
    this.backingObservable = new HotObservable<>();
    this.failure = requireNonNull(initialProblem);
  }

  @Override
  public Observable<Change<T>> changes() {
    return observer -> backingObservable
        .observe(new PassthroughObserver<Message<T>, Change<T>>(observer) {
          private Message<T> previousValue;

          @Override
          public void onObserve(Observation observation) {
            this.previousValue = getMessage();
            super.onObserve(observation);
          }

          @Override
          public void onNext(Message<T> message) {
            Message<T> previousValue = this.previousValue;
            Message<T> newValue = message;
            this.previousValue = newValue;

            getDownstreamObserver().onNext(new Change<T>() {
              @Override
              public ObservableValue<T> previousValue() {
                return previousValue.materialize();
              }

              @Override
              public ObservableValue<T> newValue() {
                return newValue.materialize();
              }
            });
          }
        });
  }

  public Message<T> getMessage() {
    if (value != null) {
      return new Message<>(value);
    } else {
      return new Message<>(failure);
    }
  }

  @Override
  public Observable<T> value() {
    return backingObservable
        .prefixing(this::getMessage)
        .asserting(Message::failure)
        .map(Message::value)
        .map(Optional::get);
  }

  @Override
  public Observable<Optional<T>> optionalValue() {
    return backingObservable.prefixing(this::getMessage).map(Message::value);
  }

  @Override
  public synchronized T set(T value) {
    if (failure == null && Objects.equals(this.value, value)) {
      return value;
    }

    T previous = this.value;
    failure = null;
    this.value = value;

    backingObservable.next(new Message<>(value));

    return previous;
  }

  @Override
  public synchronized void setProblem(Supplier<Throwable> t) {
    value = null;
    failure = t;

    backingObservable.next(new Message<>(t));
  }

  @Override
  public synchronized T get() {
    if (value == null)
      throw new MissingValueException(this, failure.get());
    return value;
  }

  @Override
  public Optional<T> tryGet() {
    return Optional.ofNullable(value);
  }
}
