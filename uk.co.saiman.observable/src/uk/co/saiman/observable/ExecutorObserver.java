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

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;

public class ExecutorObserver<T> extends SafeObserver<T> {
  private final Executor executor;

  public ExecutorObserver(Observer<? super T> downstreamObserver, Executor executor) {
    super(downstreamObserver);

    this.executor = requireNonNull(executor);
  }

  @Override
  public void onNext(T message) {
    executor.execute(() -> super.onNext(message));
  }

  @Override
  public void onObserve(Observation observation) {
    executor.execute(() -> super.onObserve(observation));
  }

  @Override
  public void onComplete() {
    executor.execute(() -> super.onComplete());
  }

  @Override
  public void onFail(Throwable t) {
    executor.execute(() -> super.onFail(t));
  }
}
