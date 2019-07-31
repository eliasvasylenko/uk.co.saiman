package uk.co.saiman.experiment.service;

import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.source.Provision;

public interface UnlimitedResourceProvider {
  Stream<? extends Provision<?>> unlimitedProvisions();

  <T> T provideValue(Provision<T> provision);
}
