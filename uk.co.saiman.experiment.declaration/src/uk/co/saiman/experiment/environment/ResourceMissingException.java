package uk.co.saiman.experiment.environment;

import uk.co.saiman.experiment.dependency.source.Provision;

public class ResourceMissingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceMissingException(Provision<?> provision) {
    super(provision.id());
  }
}
