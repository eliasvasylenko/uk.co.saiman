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

import java.util.Optional;
import java.util.function.Predicate;

/**
 * A value which can be {@link #get() fetched} and observed for updates and
 * {@link #changes() changes}.
 * <p>
 * Observable value adheres to the contract of {@link Observable}, with a few
 * extra restrictions on its behavior to conform to the semantics of a mutable
 * value.
 * <p>
 * An instance may send a {@link Observer#onComplete() completion} event to
 * indicate that it will not mutate beyond that point, but it should only send
 * this to an observer if it has already sent them a message event containing
 * the current value.
 * <p>
 * The value may enter an error state, in which case observers will receive a
 * {@link Observer#onFail(Throwable) failure event} and subsequent calls to
 * {@link #get()} should throw a {@link MissingValueException} as per
 * {@link Observable#get()} until a new valid value is available.
 * <p>
 * A common example of an error state may be {@link NullPointerException} when
 * there is no value available.
 * <p>
 * The observable should always be primed with either a message event or a
 * failure event immediately upon instantiation, and observers which subscribe
 * to the observable should always receive the last such event immediately upon
 * the first request made. Typically implementations may not support
 * backpressure, meaning a signal will always be propagated immediately upon
 * observation.
 * <p>
 * This means that the {@link #get()} method inherited from observable should
 * never block.
 * <p>
 * Invocation of {@link #get()} should be idempotent and always reflect the
 * current up-to-date state of the value. Invocation of
 * {@link #observe(Observer)} should also be idempotent.
 * <p>
 * This does <em>not</em> mean that implementations of {@link ObservableValue}
 * can never support backpressure, but it does mean that at any given moment the
 * implementation should only be prepared to fulfill a single request with the
 * latest value, and previous signals are discarded.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the value
 */
public interface ObservableValue<T> extends Observable<T> {
  /**
   * A value change event.
   * 
   * @author Elias N Vasylenko
   *
   * @param <T>
   *          the type of the value
   */
  interface Change<T> {
    ObservableValue<T> previousValue();

    ObservableValue<T> newValue();
  }

  /**
   * Immediately resolve the current value if one exists, otherwise throw a
   * {@link MissingValueException} with a cause representing the current failure
   * state.
   * 
   * @return the current value
   */
  @Override
  T get();

  @Override
  default ObservableValue<T> toValue() {
    return this;
  }

  @Override
  default ObservableValue<T> toValue(T initial) {
    return this;
  }

  @Override
  default ObservableValue<T> toValue(Throwable initialProblem) {
    return this;
  }

  /**
   * Immediately resolve the current value, if one exists.
   * 
   * @return an optional containing the current value, or an empty option if no
   *         value is available
   */
  @Override
  default Optional<T> tryGet() {
    return Observable.super.tryGet();
  }

  default boolean isValid() {
    return tryGet().isPresent();
  }

  default boolean isEqual(T value) {
    return isMatching(value::equals);
  }

  default boolean isMatching(Predicate<? super T> value) {
    return tryGet().filter(value::test).isPresent();
  }

  default Throwable getProblem() {
    return tryGetProblem().orElseThrow(IllegalStateException::new);
  }

  default Optional<Throwable> tryGetProblem() {
    try {
      get();
      return Optional.empty();
    } catch (MissingValueException e) {
      return Optional.of(e.getCause() != null ? e.getCause() : e);
    } catch (Throwable t) {
      return Optional.of(t);
    }
  }

  /**
   * @return an observable over changes to the value
   */
  Observable<Change<T>> changes();
}