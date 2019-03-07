package uk.co.saiman.experiment.procedure;

public class ProcedureException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ProcedureException(String message) {
    super(message);
  }

  public ProcedureException(String message, Throwable cause) {
    super(message, cause);
  }
}
