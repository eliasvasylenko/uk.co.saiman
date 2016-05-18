package uk.co.saiman.experiment.sample;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of sample configuration for the instrument
 */
public interface SampleExperimentType<T extends SampleConfiguration> extends ExperimentType<T> {
	@Override
	default String getName() {
		return "Sample";
	}

	@Override
	default boolean mayComeAfter(ExperimentNode<?> parentNode) {
		/*
		 * by default, must be a direct descendant of the experiment root
		 */
		return !parentNode.getParent().isPresent();
	}

	@Override
	default boolean mayComeBefore(ExperimentNode<?> penultimateDescendantNode, ExperimentType<?> descendantNodeType) {
		/*
		 * by default, no restrictions
		 */
		return true;
	}
}
