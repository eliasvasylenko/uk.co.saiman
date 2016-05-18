package uk.co.saiman.experiment;

import static uk.co.strangeskies.utilities.text.Localizer.getDefaultLocalizer;

import java.util.function.Function;

import uk.co.strangeskies.utilities.text.LocalizedRuntimeException;
import uk.co.strangeskies.utilities.text.LocalizedString;

/**
 * A problem with experiment configuration or processing.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentException extends LocalizedRuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *          a function from a localised text interface to a
	 *          {@link LocalizedString}
	 * @param cause
	 *          the exception cause
	 */
	public ExperimentException(Function<ExperimentText, LocalizedString> message, Throwable cause) {
		super(message.apply(getText()), cause);
	}

	/**
	 * @param message
	 *          a function from a localised text interface to a
	 *          {@link LocalizedString}
	 */
	public ExperimentException(Function<ExperimentText, LocalizedString> message) {
		this(message, null);
	}

	/**
	 * @param message
	 *          a {@link LocalizedString} describing the exception
	 * @param cause
	 *          the exception cause
	 */
	public ExperimentException(LocalizedString message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *          a {@link LocalizedString} describing the exception
	 */
	public ExperimentException(LocalizedString message) {
		this(message, null);
	}

	protected static ExperimentText getText() {
		return getDefaultLocalizer().getLocalization(ExperimentText.class);
	}
}
