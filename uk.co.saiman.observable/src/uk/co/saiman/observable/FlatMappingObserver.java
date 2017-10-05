package uk.co.saiman.observable;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class FlatMappingObserver<T, U> extends PassthroughObserver<T, U> {
  private final RequestAllocator requestAllocator;
  private final Function<? super T, ? extends Observable<? extends U>> mapping;

  private Observation upstreamObservation;
  private final LinkedHashMap<Observer<? extends U>, Observation> observations;
  private long outstandingRequests;
  private boolean cancelled;

  public FlatMappingObserver(
      Observer<? super U> downstreamObserver,
      Function<? super T, ? extends Observable<? extends U>> mapping,
      RequestAllocator requestAllocator) {
    super(downstreamObserver);
    this.mapping = requireNonNull(mapping);
    this.observations = new LinkedHashMap<>();
    this.requestAllocator = requireNonNull(requestAllocator);
  }

  protected Object getMutex() {
    return observations;
  }

  private void allocateRequests() {
    synchronized (getMutex()) {
      if (observations.isEmpty()) {
        upstreamObservation.requestNext();
      } else {
        outstandingRequests = requestAllocator
            .allocateRequests(outstandingRequests, new ArrayList<>(observations.values()));
      }
    }
  }

  @Override
  public void onObserve(Observation observation) {
    upstreamObservation = observation;
    super.onObserve(new Observation() {
      @Override
      public void cancel() {
        observation.cancel();
        cancelled = true;
        synchronized (getMutex()) {
          observations.values().forEach(Observation::cancel);
        }
      }

      @Override
      public void request(long count) {
        synchronized (getMutex()) {
          if (outstandingRequests < Long.MAX_VALUE) {
            outstandingRequests += count;
          }
          allocateRequests();
        }
      }

      @Override
      public void requestUnbounded() {
        Observation.super.requestUnbounded();
      }

      @Override
      public void requestNext() {
        Observation.super.requestNext();
      }

      @Override
      public long getPendingRequestCount() {
        synchronized (getMutex()) {
          return observations
              .values()
              .stream()
              .mapToLong(Observation::getPendingRequestCount)
              .sum();
        }
      }
    });
  }

  @Override
  public void onNext(T message) {
    synchronized (getMutex()) {
      if (!cancelled) {
        mapping.apply(message).observe(new Observer<U>() {
          @Override
          public void onNext(U m) {
            getDownstreamObserver().onNext(m);
            synchronized (getMutex()) {
              if (observations.get(this).getPendingRequestCount() == 0) {
                allocateRequests();
              }
            }
          }

          @Override
          public void onObserve(Observation observation) {
            synchronized (getMutex()) {
              observations.put(this, observation);
              allocateRequests();
            }
          }

          @Override
          public void onComplete() {
            synchronized (getMutex()) {
              getObservation().request(observations.remove(this).getPendingRequestCount());
            }
          }

          @Override
          public void onFail(Throwable t) {
            synchronized (getMutex()) {
              observations.values().forEach(Observation::cancel);
            }
            getDownstreamObserver().onFail(t);
          }
        });
      }
    }
  }
}
