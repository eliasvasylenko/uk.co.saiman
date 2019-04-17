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

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * A simple implementation of {@link Observable} which maintains a list of
 * listeners to receive events fired with {@link #next(Object)}.
 * <p>
 * Addition and removal of observers, as well as the firing of events, can all
 * be safely performed asynchronously. By default, these actions do however
 * block until they have been completed. If effectively non-blocking behavior is
 * required, it is necessary to introduce backpressure via e.g. a buffer.
 * <p>
 * This implementation does not support backpressure, so listeners which need to
 * control demand must compose the observable with e.g. a buffering or dropping
 * operation.
 * 
 * @author Elias N Vasylenko
 * @param <M> The type of event message to produce
 */
public class HotObservable<M> implements Observable<M> {
  private boolean live = true;
  private Set<ObservationImpl<M>> observations;
  private final Executor executor;

  public HotObservable() {
    this.executor = null;
  }

  public HotObservable(Executor executor) {
    this.executor = requireNonNull(executor);
  }

  @Override
  public Disposable observe(Observer<? super M> observer) {
    return observeImpl(observer);
  }

  protected ObservationImpl<M> observeImpl(Observer<? super M> observer) {
    ObservationImpl<M> observation = new ObservationImpl<M>(observer) {
      @Override
      public void cancelImpl() {
        cancelObservation(this);
      }

      @Override
      public void request(long count) {}

      @Override
      public long getPendingRequestCount() {
        return Long.MAX_VALUE;
      }
    };

    synchronized (this) {
      boolean opened = observations == null;

      if (opened) {
        observations = new LinkedHashSet<>();
      }
      observations.add(observation);

      if (isLive()) {
        forObservers(singletonList(observation), ObservationImpl::onObserve);

        if (opened) {
          try {
            open();
          } catch (Throwable e) {
            this.observations = null;
            observation.onFail(e);
            return observation;
          }
        }
      }
    }

    return observation;
  }

  protected void open() throws Exception {}

  protected void close() throws Exception {}

  public boolean hasObservers() {
    return observations != null;
  }

  synchronized void cancelObservation(Observation observer) {
    if (observations != null && observations.remove(observer) && observations.isEmpty()) {
      observations = null;
      try {
        close();
      } catch (Exception e) {
        ((ObservationImpl<?>) observer).onFail(e);
      }
    }
  }

  private void forObservers(
      List<ObservationImpl<M>> observations,
      Consumer<ObservationImpl<M>> action) {
    if (observations != null) {
      if (executor == null) {
        for (ObservationImpl<M> observation : observations) {
          action.accept(observation);
        }
      } else {
        CountDownLatch latch = new CountDownLatch(observations.size());

        for (ObservationImpl<M> observation : observations) {
          executor.execute(() -> {
            try {
              action.accept(observation);
            } finally {
              latch.countDown();
            }
          });
        }

        try {
          latch.await();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public synchronized boolean isLive() {
    return live;
  }

  void assertLive() {
    if (!live)
      throw new IllegalStateException();
  }

  public synchronized HotObservable<M> start() {
    if (!live) {
      live = true;

      forObservers(copyObservations(), o -> o.onObserve());
    }

    return this;
  }

  /**
   * Fire the given message to all observers.
   * 
   * @param item the message event to send
   * @return the receiver for method chaining
   */
  public synchronized HotObservable<M> next(M item) {
    Objects.requireNonNull(item);

    assertLive();

    forObservers(copyObservations(), o -> o.onNext(item));

    return this;
  }

  public synchronized HotObservable<M> complete() {
    assertLive();
    live = false;

    List<ObservationImpl<M>> observations = copyObservations();
    this.observations = null;
    forObservers(observations, o -> o.onComplete());

    return this;
  }

  public synchronized HotObservable<M> fail(Throwable t) {
    Objects.requireNonNull(t);

    assertLive();
    live = false;

    List<ObservationImpl<M>> observations = copyObservations();
    this.observations = null;
    forObservers(observations, o -> o.onFail(t));

    return this;
  }

  private List<ObservationImpl<M>> copyObservations() {
    return this.observations != null ? new ArrayList<>(this.observations) : null;
  }
}
