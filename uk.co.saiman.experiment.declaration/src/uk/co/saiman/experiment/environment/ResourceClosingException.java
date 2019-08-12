package uk.co.saiman.experiment.environment;

import uk.co.saiman.experiment.dependency.source.Provision;

public class ResourceClosingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceClosingException(Provision<?> provision) {
    super(provision.id());
  }
}
