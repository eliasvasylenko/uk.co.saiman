package uk.co.saiman.msapex.experiment;

import uk.co.strangeskies.fx.TreeContribution;

/**
 * A source of a type of {@link TreeContribution contribution} for the
 * {@link ExperimentTreeController experiment tree}. The contribution class
 * returned from {@link #getContribution()} should be instantiable by Eclipse
 * injector.
 * 
 * @author Elias N Vasylenko
 */
public interface ExperimentTreeContributor {
	/**
	 * @return a tree contribution to be instantiated by the Eclipse context
	 *         injector on behalf of the {@link ExperimentTreeController}.
	 */
	Class<? extends TreeContribution<?>> getContribution();
}
