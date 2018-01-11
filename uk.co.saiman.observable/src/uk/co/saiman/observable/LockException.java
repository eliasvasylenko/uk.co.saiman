package uk.co.saiman.observable;

public class LockException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public LockException(Exception e) {
    super(e);
  }
}
