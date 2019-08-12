package uk.co.saiman.experiment.environment;

import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.source.Provision;

public interface GlobalEnvironment {
  Stream<Provision<?>> providedValues();

  default boolean providesValue(Provision<?> provision) {
    return providedValues().anyMatch(provision::equals);
  }

  <T> T provideValue(Provision<T> provision);
}
