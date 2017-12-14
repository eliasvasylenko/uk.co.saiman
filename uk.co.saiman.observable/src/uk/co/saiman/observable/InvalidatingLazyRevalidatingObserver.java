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

public class InvalidatingLazyRevalidatingObserver<M>
    extends BackpressureReducingObserver<M, Invalidation<M>> {
  private M latest;
  private Invalidation<M> invalidation;
  private Observation intermediateObservation;

  public InvalidatingLazyRevalidatingObserver(
      Observer<? super Invalidation<M>> downstreamObserver) {
    super(downstreamObserver);
  }

  @Override
  public void onObserve(Observation observation) {
    super.onObserve(observation);
    intermediateObservation.requestNext();
  }

  @Override
  protected Observation createDownstreamObservation(Observation observation) {
    intermediateObservation = super.createDownstreamObservation(observation);
    return new Observation() {
      @Override
      public void cancel() {
        intermediateObservation.cancel();
      }

      @Override
      public void request(long count) {}

      @Override
      public long getPendingRequestCount() {
        return Long.MAX_VALUE;
      }
    };
  }

  @Override
  public Invalidation<M> initialize(M message) {
    synchronized (this) {
      latest = message;
      invalidation = new Invalidation<M>() {
        private boolean done;

        @Override
        public M revalidate() {
          if (done)
            throw new IllegalStateException();
          done = true;
          synchronized (this) {
            intermediateObservation.requestNext();
            return latest;
          }
        }
      };
      return invalidation;
    }
  }

  @Override
  public Invalidation<M> accumulate(Invalidation<M> current, M message) {
    synchronized (this) {
      latest = message;
      return invalidation;
    }
  }
}
