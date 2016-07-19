package uk.co.saiman.experiment;

import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.Properties;

public interface ExperimentExceptionProperties extends Properties<ExperimentExceptionProperties> {
	/**
	 * @param descendantType
	 *          the type of the descendant we wish to add
	 * @param ancestorNode
	 *          an ancestor of the candidate node
	 * @return a node of the given type may not be a descendant of the given node
	 */
	Localized<String> typeMayNotSucceed(ExperimentType<?> descendantType, ExperimentNode<?, ?> ancestorNode);

	Localized<String> experimentIsDisposed(ExperimentNode<?, ?> experimentNode);

	Localized<String> illegalContextMenuFor(Object context);

	Localized<String> experimentDoesNotExist(ExperimentNode<?, ?> experimentNode);
}
