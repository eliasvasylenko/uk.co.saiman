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

/**
 * This is a helper class for implementing {@link Observable passive
 * observables}.
 * <p>
 * A passive observable is one which does not maintain a set of observations or
 * manage its own events, instead deferring to one or more upstream observables.
 * When an observer subscribes to a passive observable, typically the observer
 * is decorated, and the decorator is then subscribed to the parents. This way
 * the decorator can modify, inspect, or filter events as appropriate before
 * passing them back through to the original observer.
 * <p>
 * This class is a partial implementation of such a decorator.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> The message type of the upstream observable
 */
public abstract class ExclusiveObserver<T> implements Observer<T> {
  private Observation observation;

  public Observation getObservation() {
    return observation;
  }

  @Override
  public abstract void onNext(T message);

  protected void initializeObservation(Observation observation) {
    if (this.observation != null) {
      throw new AlreadyObservedException();
    }
    this.observation = requireNonNull(observation);
  }

  @Override
  public void onObserve(Observation observation) {
    initializeObservation(observation);
  }
}
