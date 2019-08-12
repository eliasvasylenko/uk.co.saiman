package uk.co.saiman.experiment.environment.service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.LocalEnvironment;

public interface LocalEnvironmentService {
  LocalEnvironment openLocalEnvironment(
      GlobalEnvironment globalEnvironment,
      Collection<? extends Provision<?>> resources,
      long timeout,
      TimeUnit unit);
}
