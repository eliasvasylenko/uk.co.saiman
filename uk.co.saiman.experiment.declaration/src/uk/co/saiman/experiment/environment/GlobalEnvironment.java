package uk.co.saiman.experiment.environment;

import java.util.stream.Stream;

public interface GlobalEnvironment {
  Stream<Class<?>> providedValues();

  default boolean providesValue(Class<?> provision) {
    return providedValues().anyMatch(provision::equals);
  }

  <T> T provideValue(Class<T> provision);
}
