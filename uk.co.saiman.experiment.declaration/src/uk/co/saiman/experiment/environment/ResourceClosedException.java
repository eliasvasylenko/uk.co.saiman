package uk.co.saiman.experiment.environment;

import uk.co.saiman.experiment.dependency.source.Provision;

public class ResourceClosedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceClosedException(Provision<?> provision) {
    super(provision.id());
  }
}
