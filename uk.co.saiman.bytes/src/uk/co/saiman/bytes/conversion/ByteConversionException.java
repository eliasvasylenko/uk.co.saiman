package uk.co.saiman.bytes.conversion;

public class ByteConversionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ByteConversionException(String message) {
    super(message);
  }

  public ByteConversionException(String message, Throwable cause) {
    super(message, cause);
  }
}
