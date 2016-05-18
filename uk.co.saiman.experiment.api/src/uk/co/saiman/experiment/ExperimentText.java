package uk.co.saiman.experiment;

import uk.co.strangeskies.utilities.text.AppendToLocalizationKey;
import uk.co.strangeskies.utilities.text.LocalizedString;
import uk.co.strangeskies.utilities.text.LocalizedText;

/**
 * {@link LocalizedText} interface for texts relating to experiments.
 * 
 * @author Elias N Vasylenko
 */
@SuppressWarnings("javadoc")
public interface ExperimentText extends LocalizedText<ExperimentText> {
	LocalizedString newExperiment();

	LocalizedString newExperimentName();

	/**
	 * @param state
	 *          the state to localise
	 * @return localised name of the state
	 */
	LocalizedString lifecycleState(@AppendToLocalizationKey ExperimentLifecycleState state);

	/**
	 * @param descendantType
	 *          the type of the descendant we wish to add
	 * @param ancestorNode
	 *          an ancestor of the candidate node
	 * @return a node of the given type may not be a descendant of the given node
	 */
	LocalizedString typeMayNotSucceed(ExperimentType<?> descendantType, ExperimentNode<?> ancestorNode);

	LocalizedString experimentIsDisposed(ExperimentNode<?> experimentNodeImpl);

	LocalizedString illegalContextMenuFor(Object context);
}
