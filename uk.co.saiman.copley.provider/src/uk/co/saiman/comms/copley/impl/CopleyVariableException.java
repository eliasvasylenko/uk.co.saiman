package uk.co.saiman.comms.copley.impl;

public class CopleyVariableException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CopleyVariableException(String message, Exception cause) {
    super(message, cause);
  }
}
