package uk.co.saiman.acquisition;

import uk.co.strangeskies.utilities.text.LocalizedString;
import uk.co.strangeskies.utilities.text.LocalizedText;

/**
 * Localised text resource accessor for acquisition engine items.
 * 
 * @author Elias N Vasylenko
 */
@SuppressWarnings("javadoc")
public interface AcquisitionText extends LocalizedText<AcquisitionText> {
	LocalizedString device();

	LocalizedString devices();

	LocalizedString noDevices();

	LocalizedString alreadyAcquiring();

	LocalizedString experimentInterrupted();

	LocalizedString unexpectedException();

	LocalizedString countMustBePositive();

	LocalizedString noSignal();
}
