package uk.co.saiman.experiment.environment;

import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.source.Provision;

public interface LocalEnvironment extends AutoCloseable {
  GlobalEnvironment getGlobalEnvironment();

  Stream<Provision<?>> providedResources();

  <T> Resource<T> provideResource(Provision<T> provision);
}
