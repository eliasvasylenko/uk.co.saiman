package uk.co.saiman.observable;

import java.util.function.Supplier;

public class PrefixingObserver<T> extends PassthroughObserver<T, T> {
  private final Supplier<T> prefix;

  PrefixingObserver(Supplier<T> prefix, Observer<? super T> downstreamObserver) {
    super(downstreamObserver);
    this.prefix = prefix;
  }

  @Override
  public void onObserve(Observation observation) {
    super.onObserve(observation);

    try {
      getDownstreamObserver().onNext(prefix.get());
    } catch (Exception e) {
      getDownstreamObserver().onFail(e);
    }
  }

  @Override
  public void onNext(T message) {
    getDownstreamObserver().onNext(message);
  }
}