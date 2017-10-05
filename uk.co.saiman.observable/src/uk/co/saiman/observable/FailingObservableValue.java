/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

public class FailingObservableValue<T> implements ObservableValue<T> {
  private final Throwable failure;

  public FailingObservableValue(Throwable failure) {
    this.failure = failure;
  }

  @Override
  public Disposable observe(Observer<? super T> observer) {
    ObservationImpl<T> observation = new ObservationImpl<T>(observer) {
      @Override
      public synchronized void request(long count) {}

      @Override
      public synchronized long getPendingRequestCount() {
        return Long.MAX_VALUE;
      }

      @Override
      protected void cancelImpl() {}
    };
    observation.onObserve();
    observation.onFail(failure);
    return observation;
  }

  @Override
  public T get() {
    throw new MissingValueException(this, failure);
  }

  @Override
  public Observable<Change<T>> changes() {
    return new EmptyObservable<>();
  }
}
