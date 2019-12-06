package uk.co.saiman.instrument.acquisition.adq;

public class AdqException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public AdqException(Exception cause) {
    super(cause);
  }

  public AdqException(String message) {
    super(message);
  }

  public AdqException(String message, Exception cause) {
    super(message, cause);
  }
}
