package uk.co.saiman.experiment.service;

import uk.co.saiman.experiment.environment.Provision;
import uk.co.saiman.experiment.environment.Resource;

public interface ResourceProvider<T> {
  Provision<T> provision();

  Resource<T> provideResource();
}
