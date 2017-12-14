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

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BackpressureReducingObserver<T, M> extends PassthroughObserver<T, M> {
  private M current;
  private boolean complete;
  private final RequestCount outstandingRequests = new RequestCount();

  public BackpressureReducingObserver(Observer<? super M> downstreamObserver) {
    super(downstreamObserver);
  }

  public static <T, M> BackpressureReducingObserver<T, M> backpressureReducingObserver(
      Observer<? super M> downstreamObserver,
      Supplier<? extends M> identity,
      BiFunction<? super M, ? super T, ? extends M> accumulator) {
    requireNonNull(identity);
    requireNonNull(accumulator);

    return new BackpressureReducingObserver<T, M>(downstreamObserver) {
      @Override
      public M initialize(T message) {
        return accumulate(identity.get(), message);
      }

      @Override
      public M accumulate(M current, T message) {
        return accumulator.apply(current, message);
      }
    };
  }

  public static <T, M> BackpressureReducingObserver<T, M> backpressureReducingObserver(
      Observer<? super M> downstreamObserver,
      Function<? super T, ? extends M> initial,
      BiFunction<? super M, ? super T, ? extends M> accumulator) {
    requireNonNull(initial);
    requireNonNull(accumulator);

    return new BackpressureReducingObserver<T, M>(downstreamObserver) {
      @Override
      public M initialize(T message) {
        return initial.apply(message);
      }

      @Override
      public M accumulate(M current, T message) {
        return accumulator.apply(current, message);
      }
    };
  }

  @Override
  public void onObserve(Observation observation) {
    super.onObserve(createDownstreamObservation(observation));
    observation.requestUnbounded();
  }

  protected Observation createDownstreamObservation(Observation observation) {
    return new Observation() {
      @Override
      public void requestNext() {
        request(1);
      }

      @Override
      public void request(long count) {
        synchronized (outstandingRequests) {
          outstandingRequests.request(count);

          if (current != null) {
            if (complete) {
              current = null;
              getDownstreamObserver().onComplete();
            } else if (count > 0) {
              sendNext();
            }
          }
        }
      }

      @Override
      public boolean isRequestUnbounded() {
        return Observation.super.isRequestUnbounded();
      }

      @Override
      public void cancel() {
        observation.cancel();
      }

      @Override
      public long getPendingRequestCount() {
        return outstandingRequests.getCount();
      }
    };
  }

  public abstract M initialize(T message);

  public abstract M accumulate(M current, T message);

  private void sendNext() {
    synchronized (outstandingRequests) {
      outstandingRequests.fulfil();
      M message = current;
      current = null;
      getDownstreamObserver().onNext(message);
    }
  }

  @Override
  public void onNext(T message) {
    synchronized (outstandingRequests) {
      if (current == null)
        current = initialize(message);
      else
        current = accumulate(current, message);

      if (!outstandingRequests.isFulfilled()) {
        sendNext();
      }
    }
  }

  @Override
  public void onComplete() {
    synchronized (outstandingRequests) {
      complete = true;
      if (current == null) {
        getDownstreamObserver().onComplete();
      }
    }
  }

  @Override
  public void onFail(Throwable t) {
    synchronized (outstandingRequests) {
      complete = true;
      current = null;
      getDownstreamObserver().onFail(t);
    }
  }
}
