package uk.co.saiman.experiment.service;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.source.Provision;

public interface LimitedResourceProvider {
  Stream<? extends Provision<?>> limitedProvisions();

  <T> Resource<T> provideResource(Provision<T> provision, long timeout, TimeUnit unit);
}
