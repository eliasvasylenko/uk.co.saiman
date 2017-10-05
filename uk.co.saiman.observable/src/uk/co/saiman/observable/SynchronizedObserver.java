package uk.co.saiman.observable;

import static java.util.Objects.requireNonNull;

public class SynchronizedObserver<M> extends PassthroughObserver<M, M> {
  private Object mutex;

  public SynchronizedObserver(Observer<? super M> downstreamObserver, Object mutex) {
    super(downstreamObserver);

    this.mutex = requireNonNull(mutex);
  }

  @Override
  public void onNext(M message) {
    synchronized (mutex) {
      getDownstreamObserver().onNext(message);
    }
  }

  @Override
  public void onComplete() {
    synchronized (mutex) {
      super.onComplete();
    }
  }

  @Override
  public void onFail(Throwable t) {
    synchronized (mutex) {
      super.onFail(t);
    }
  }

  @Override
  public void onObserve(Observation observation) {
    synchronized (mutex) {
      super.onObserve(new Observation() {
        @Override
        public void cancel() {
          synchronized (mutex) {
            observation.cancel();
          }
        }

        @Override
        public void request(long count) {
          synchronized (mutex) {
            observation.request(count);
          }
        }

        @Override
        public long getPendingRequestCount() {
          synchronized (mutex) {
            return observation.getPendingRequestCount();
          }
        }
      });
    }
  }
}
