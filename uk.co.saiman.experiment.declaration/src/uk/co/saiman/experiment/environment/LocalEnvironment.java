package uk.co.saiman.experiment.environment;

import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Resource;

public interface LocalEnvironment extends AutoCloseable {
  GlobalEnvironment getGlobalEnvironment();

  Stream<Class<?>> providedResources();

  default boolean providesResource(Class<?> provision) {
    return providedResources().anyMatch(provision::equals);
  }

  <T> Resource<T> provideResource(Class<T> provision);
}
