package uk.co.saiman.instrument.simulation;

import uk.co.saiman.acquisition.AcquisitionText;
import uk.co.strangeskies.utilities.text.LocalizedString;
import uk.co.strangeskies.utilities.text.LocalizedText;

@SuppressWarnings("javadoc")
public interface SimulationText extends LocalizedText<SimulationText> {
	LocalizedString xyRasterStageSimulationDeviceName();

	LocalizedString acquisitionSimulationDeviceName();

	AcquisitionText acquisition();
}
