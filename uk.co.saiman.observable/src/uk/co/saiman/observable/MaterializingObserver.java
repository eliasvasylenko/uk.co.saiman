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

public class MaterializingObserver<T> extends PassthroughObserver<T, ObservableValue<T>> {
  public MaterializingObserver(Observer<? super ObservableValue<T>> downstreamObserver) {
    super(downstreamObserver);
  }

  @Override
  public void onObserve(Observation observation) {
    /*
     * TODO deal with backpressure properly! Consider that the extra "onNext" from
     * "onFail" may not be requested.
     */
    super.onObserve(observation);
  }

  @Override
  public void onNext(T message) {
    getDownstreamObserver().onNext(ObservableValue.of(message));
  }

  @Override
  public void onFail(Throwable t) {
    getDownstreamObserver().onNext(ObservableValue.empty(() -> t));

    super.onComplete();
  }

  @Override
  public void onComplete() {
    super.onComplete();
  }
}
