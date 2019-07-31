package uk.co.saiman.experiment.environment;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.source.Provision;

public interface SharedEnvironment {
  Stream<Provision<?>> providedValues();

  boolean providesValue(Provision<?> provision);

  <T> T provideValue(Provision<T> provision);

  default <T> Optional<T> provideValueOptionally(Provision<T> provision) {
    return providesValue(provision) ? Optional.of(provideValue(provision)) : Optional.empty();
  }

  LocalEnvironment openLocalEnvironment(
      Collection<? extends Provision<?>> resources,
      long timeout,
      TimeUnit unit);
}
