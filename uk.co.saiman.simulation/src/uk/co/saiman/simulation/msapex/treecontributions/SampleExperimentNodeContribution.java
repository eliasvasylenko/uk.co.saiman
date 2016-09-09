package uk.co.saiman.simulation.msapex.treecontributions;

import java.util.Arrays;
import java.util.List;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.simulation.experiment.SimulatedSampleImageConfiguration;
import uk.co.strangeskies.fx.TreeChildContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.reflection.TypeToken;
import uk.co.strangeskies.reflection.TypedObject;

@SuppressWarnings("javadoc")
public class SampleExperimentNodeContribution
		implements TreeChildContribution<ExperimentNode<?, ? extends SimulatedSampleImageConfiguration>> {
	@Override
	public <U extends ExperimentNode<?, ? extends SimulatedSampleImageConfiguration>> boolean hasChildren(
			TreeItemData<U> data) {
		return true;
	}

	@Override
	public <U extends ExperimentNode<?, ? extends SimulatedSampleImageConfiguration>> List<TypedObject<?>> getChildren(
			TreeItemData<U> data) {
		return Arrays.asList(new TypeToken<SimulatedSampleImageConfiguration>() {}.typedObject(data.data().getState()));
	}
}