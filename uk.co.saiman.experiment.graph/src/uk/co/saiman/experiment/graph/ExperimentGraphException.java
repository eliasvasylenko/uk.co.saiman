package uk.co.saiman.experiment.graph;

public class ExperimentGraphException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ExperimentGraphException(String message) {
    super(message);
  }

  public ExperimentGraphException(String message, Throwable cause) {
    super(message, cause);
  }
}
