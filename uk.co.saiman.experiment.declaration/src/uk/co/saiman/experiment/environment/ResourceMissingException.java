package uk.co.saiman.experiment.environment;

public class ResourceMissingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceMissingException(Class<?> provision) {
    super(provision.getName());
  }
}
