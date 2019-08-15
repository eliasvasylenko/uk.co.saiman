package uk.co.saiman.experiment.environment;

public class ResourceClosedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ResourceClosedException(Class<?> provision) {
    super(provision.getName());
  }
}
