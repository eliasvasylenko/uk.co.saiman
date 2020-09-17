/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An observer over one or more {@link Observable} instances.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of message to observe
 */
public interface Observer<T> {
  /**
   * The method which will receive notification from an {@link Observable}.
   * 
   * @param message the message object instance
   */
  void onNext(T message);

  default void onObserve(Observation observation) {}

  default void onComplete() {}

  default void onFail(Throwable t) {}

  static <T> Observer<T> onObservation(Consumer<Observation> action) {
    return new Observer<T>() {
      @Override
      public void onNext(T message) {}

      @Override
      public void onObserve(Observation observation) {
        action.accept(observation);
      }
    };
  }

  static <T> Observer<T> onCompletion(Runnable action) {
    return new Observer<T>() {
      @Override
      public void onNext(T message) {}

      @Override
      public void onComplete() {
        action.run();
      }
    };
  }

  static <T> Observer<T> onFailure(Consumer<Throwable> action) {
    return new Observer<T>() {
      @Override
      public void onNext(T message) {}

      @Override
      public void onFail(Throwable t) {
        action.accept(t);
      }
    };
  }

  static <T> Observer<T> forObservation(Function<Observation, Observer<T>> observerProvider) {
    return new Observer<T>() {
      Observer<T> observer;

      @Override
      public void onObserve(Observation observation) {
        observer = observerProvider.apply(observation);
        observer.onObserve(observation);
      }

      @Override
      public void onNext(T message) {
        observer.onNext(message);
      }

      @Override
      public void onComplete() {
        observer.onComplete();
      }

      @Override
      public void onFail(Throwable t) {
        observer.onFail(t);
      }
    };
  }
}
