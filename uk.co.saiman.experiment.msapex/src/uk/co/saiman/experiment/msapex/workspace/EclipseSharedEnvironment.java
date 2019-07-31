package uk.co.saiman.experiment.msapex.workspace;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.environment.SharedEnvironment;

public class EclipseSharedEnvironment implements SharedEnvironment {
  private final SharedEnvironment sharedEnvironment;

  public EclipseSharedEnvironment(SharedEnvironment sharedEnvironment) {
    this.sharedEnvironment = sharedEnvironment;
  }

  @Override
  public Stream<Provision<?>> providedValues() {
    return sharedEnvironment.providedValues();
  }

  @Override
  public boolean providesValue(Provision<?> provision) {
    return sharedEnvironment.providesValue(provision);
  }

  @Override
  public <T> T provideValue(Provision<T> provision) {
    return sharedEnvironment.provideValue(provision);
  }

  @Override
  public LocalEnvironment openLocalEnvironment(
      Collection<? extends Provision<?>> resources,
      long timeout,
      TimeUnit unit) {
    return sharedEnvironment.openLocalEnvironment(resources, timeout, unit);
  }
}
