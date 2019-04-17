package uk.co.saiman.observable;

public class FailingObservable<T> implements Observable<T> {
  private final Throwable cause;

  public FailingObservable(Throwable cause) {
    this.cause = cause;
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
    observation.onFail(cause);
    return observation;
  }
}
