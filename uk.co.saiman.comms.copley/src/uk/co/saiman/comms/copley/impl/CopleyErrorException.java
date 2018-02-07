package uk.co.saiman.comms.copley.impl;

import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.copley.ErrorCode;

public class CopleyErrorException extends CommsException {
  private static final long serialVersionUID = 1L;

  private final ErrorCode code;

  public CopleyErrorException(ErrorCode code) {
    super("Copley command error: " + code);
    this.code = code;
  }

  public ErrorCode getCode() {
    return code;
  }
}
