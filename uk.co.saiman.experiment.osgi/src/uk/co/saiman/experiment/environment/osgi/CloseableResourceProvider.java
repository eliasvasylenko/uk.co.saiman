package uk.co.saiman.experiment.environment.osgi;

import static java.util.function.Function.identity;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.experiment.environment.GlobalEnvironment;

public interface CloseableResourceProvider<T extends AutoCloseable>
    extends ExclusiveResourceProvider<T> {
  T deriveValue(GlobalEnvironment globalEnvironment, long timeout, TimeUnit unit) throws Exception;

  @Override
  default ExclusiveResource<T> deriveResource(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception {
    var value = deriveValue(globalEnvironment, timeout, unit);
    return new ExclusiveResource<>(value, identity());
  }
}