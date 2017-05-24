package uk.co.saiman.comms.copley.impl;

import uk.co.saiman.comms.CommsException;

public class CopleyCommsException extends CommsException {
	public CopleyCommsException(String message) {
		super(message);
	}

	public CopleyCommsException(String message, Exception exception) {
		super(message, exception);
	}
}
