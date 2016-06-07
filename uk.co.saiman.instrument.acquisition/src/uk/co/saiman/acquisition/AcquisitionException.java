package uk.co.saiman.acquisition;

import uk.co.strangeskies.utilities.text.LocalizedRuntimeException;
import uk.co.strangeskies.utilities.text.LocalizedString;

public class AcquisitionException extends LocalizedRuntimeException {
	public AcquisitionException(LocalizedString message) {
		super(message);
	}

	public AcquisitionException(LocalizedString message, Throwable cause) {
		super(message, cause);
	}
}
