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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FixedRateObserver<T> extends PassthroughObserver<T, T> {
  private ScheduledFuture<?> future;
  private long delay;
  private long period;
  private TimeUnit time;

  public FixedRateObserver(
      Observer<? super T> downstreamObserver,
      long delay,
      long period,
      TimeUnit time) {
    super(downstreamObserver);
    this.delay = delay;
    this.period = period;
    this.time = time;
  }

  @Override
  public void onObserve(Observation observation) {
    super.onObserve(new Observation() {
      @Override
      public void cancel() {
        observation.cancel();
        future.cancel(true);
      }

      @Override
      public void request(long count) {
        observation.request(count);
      }

      @Override
      public long getPendingRequestCount() {
        return observation.getPendingRequestCount();
      }
    });
    future = Executors
        .newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(() -> getObservation().requestNext(), delay, period, time);
  }

  @Override
  public void onComplete() {
    future.cancel(true);
    super.onComplete();
  }

  @Override
  public void onFail(Throwable t) {
    future.cancel(true);
    super.onFail(t);
  }

  @Override
  public void onNext(T message) {
    getDownstreamObserver().onNext(message);
  }
}
