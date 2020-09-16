package uk.co.saiman.maldi.legacy.queue;

public class LegacyQueueImportException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public LegacyQueueImportException(String message, Exception cause) {
    super(message, cause);
  }

  public LegacyQueueImportException(String message) {
    super(message);
  }
}
