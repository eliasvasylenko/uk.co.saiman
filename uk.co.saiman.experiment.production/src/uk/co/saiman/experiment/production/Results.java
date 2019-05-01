package uk.co.saiman.experiment.production;

import java.util.stream.Stream;

import uk.co.saiman.observable.Observable;

public interface Results {
  Stream<Result<?>> results();

  <T extends Result<?>> T resolveResult(ProductPath<?, T> path);

  <T extends Result<?>> Observable<T> results(ProductPath<?, T> path);
}
