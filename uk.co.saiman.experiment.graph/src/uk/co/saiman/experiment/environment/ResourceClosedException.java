package uk.co.saiman.experiment.environment;

public class ResourceClosedException extends Exception {
  private static final long serialVersionUID = 1L;

  public ResourceClosedException(Provision<?> provision) {
    super(provision.id());
  }
}
