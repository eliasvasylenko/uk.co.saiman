package uk.co.saiman.experiment.environment;

import uk.co.saiman.experiment.dependency.source.Provision;

public class ResourceUnavailableException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceUnavailableException(Provision<?> provision, Throwable cause) {
    super(provision.id(), cause);
  }
}
