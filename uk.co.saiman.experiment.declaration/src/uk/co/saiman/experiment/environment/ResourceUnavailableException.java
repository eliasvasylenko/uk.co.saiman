package uk.co.saiman.experiment.environment;

public class ResourceUnavailableException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceUnavailableException(Class<?> provision, Throwable cause) {
    super(provision.getName(), cause);
  }
}
