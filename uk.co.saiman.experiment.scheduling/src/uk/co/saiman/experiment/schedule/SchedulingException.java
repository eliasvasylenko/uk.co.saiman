package uk.co.saiman.experiment.schedule;

public class SchedulingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public SchedulingException(String message) {
    super(message);
  }

  public SchedulingException(String message, Throwable cause) {
    super(message, cause);
  }
}
