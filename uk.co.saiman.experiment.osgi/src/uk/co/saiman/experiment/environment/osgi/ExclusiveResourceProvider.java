package uk.co.saiman.experiment.environment.osgi;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.experiment.environment.GlobalEnvironment;

public interface ExclusiveResourceProvider<T> {
  Class<T> getProvision();

  ExclusiveResource<T> deriveResource(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception;
}