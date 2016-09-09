package uk.co.saiman.simulation.msapex.treecontributions;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.experiment.SimulatedSampleImageConfiguration;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeChildContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;
import uk.co.strangeskies.reflection.TypeToken;
import uk.co.strangeskies.reflection.TypedObject;

@SuppressWarnings("javadoc")
public class SampleImageConfigurationContribution implements TreeChildContribution<SimulatedSampleImageConfiguration>,
		TreeTextContribution<SimulatedSampleImageConfiguration>,
		PseudoClassTreeCellContribution<SimulatedSampleImageConfiguration> {
	@Inject
	@Localize
	SimulationProperties properties;

	@Override
	public <U extends SimulatedSampleImageConfiguration> boolean hasChildren(TreeItemData<U> data) {
		return true;
	}

	@Override
	public <U extends SimulatedSampleImageConfiguration> List<TypedObject<?>> getChildren(TreeItemData<U> data) {
		return Arrays.asList(

				new TypeToken<SimulatedSampleImage>() {}.typedObject(data.data().getSampleImage()),

				new TypeToken<ChemicalColor>() {}.typedObject(
						new ChemicalColor(properties.redChemical(), data.data().getRedChemical(), data.data()::setRedChemical)),

				new TypeToken<ChemicalColor>() {}.typedObject(new ChemicalColor(properties.greenChemical(),
						data.data().getGreenChemical(), data.data()::setGreenChemical)),

				new TypeToken<ChemicalColor>() {}.typedObject(
						new ChemicalColor(properties.blueChemical(), data.data().getBlueChemical(), data.data()::setBlueChemical))

		);
	}

	@Override
	public <U extends SimulatedSampleImageConfiguration> String getText(TreeItemData<U> data) {
		return properties.experiment().configuration().toString();
	}

	@Override
	public <U extends SimulatedSampleImageConfiguration> String getSupplementalText(TreeItemData<U> data) {
		return null;
	}
}