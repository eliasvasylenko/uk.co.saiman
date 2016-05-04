package uk.co.saiman.acquisition;

import uk.co.strangeskies.utilities.text.LocalizedString;
import uk.co.strangeskies.utilities.text.LocalizedText;

/**
 * Localised text resource accessor for acquisition engine items.
 * 
 * @author Elias N Vasylenko
 */
public interface AcquisitionText extends LocalizedText<AcquisitionText> {
	LocalizedString device();

	LocalizedString devices();

	LocalizedString noDevices();
}
