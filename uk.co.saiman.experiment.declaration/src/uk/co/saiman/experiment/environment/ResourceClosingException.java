package uk.co.saiman.experiment.environment;

public class ResourceClosingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceClosingException(Class<?> provision) {
    super(provision.getName());
  }
}
