package uk.co.saiman.experiment.environment;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface EnvironmentService {
  public Environment openEnvironment(
      Collection<? extends Provision<?>> provisions,
      long timeout,
      TimeUnit unit);

  public StaticEnvironment getStaticEnvironment();
}
