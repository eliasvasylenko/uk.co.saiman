package uk.co.saiman.comms;

public class CommsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CommsException(String message) {
		super(message);
	}

	public CommsException(String message, Exception exception) {
		super(message, exception);
	}
}
