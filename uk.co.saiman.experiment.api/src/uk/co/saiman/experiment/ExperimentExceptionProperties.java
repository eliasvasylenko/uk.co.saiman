package uk.co.saiman.experiment;

import uk.co.saiman.SaiProperties;
import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.Properties;
import uk.co.strangeskies.text.properties.PropertyConfiguration;
import uk.co.strangeskies.text.properties.PropertyConfiguration.KeyCase;

@PropertyConfiguration(keyCase = KeyCase.LOWER, keySplitString = ".")
public interface ExperimentExceptionProperties extends Properties<ExperimentExceptionProperties> {
	SaiProperties sai();

	/**
	 * @param descendantType
	 *          the type of the descendant we wish to add
	 * @param ancestorNode
	 *          an ancestor of the candidate node
	 * @return a node of the given type may not be a descendant of the given node
	 */
	Localized<String> typeMayNotSucceed(ExperimentType<?> descendantType, ExperimentNode<?, ?> ancestorNode);

	Localized<String> experimentIsDisposed(ExperimentNode<?, ?> experimentNode);

	Localized<String> illegalCommandForSelection(String commandId, Object selection);

	Localized<String> illegalMenuForSelection(String commandId, Object selection);

	Localized<String> experimentDoesNotExist(ExperimentNode<?, ?> experimentNode);

	Localized<String> invalidExperimentName(String name);

	Localized<String> cannotProcessExperimentConcurrently(ExperimentNode<RootExperiment, ExperimentConfiguration> root);
}
