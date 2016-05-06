package uk.co.saiman.experiment;

import uk.co.strangeskies.utilities.text.AppendToLocalizationKey;
import uk.co.strangeskies.utilities.text.LocalizedString;
import uk.co.strangeskies.utilities.text.LocalizedText;

public interface ExperimentText extends LocalizedText<ExperimentText> {
	LocalizedString newExperiment();

	LocalizedString newExperimentName();

	LocalizedString lifecycleState(@AppendToLocalizationKey ExperimentLifecycleState state);
}
