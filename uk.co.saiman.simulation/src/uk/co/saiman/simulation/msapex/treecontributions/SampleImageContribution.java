package uk.co.saiman.simulation.msapex.treecontributions;

import java.util.Objects;

import javax.inject.Inject;

import javafx.scene.Node;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.saiman.simulation.msapex.ChooseSimulatedSampleImage;
import uk.co.strangeskies.eclipse.CommandTreeCellContribution;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;

@SuppressWarnings("javadoc")
public class SampleImageContribution extends CommandTreeCellContribution<SimulatedSampleImage>
		implements PseudoClassTreeCellContribution<SimulatedSampleImage>, TreeTextContribution<SimulatedSampleImage> {
	@Inject
	@Localize
	SimulationProperties properties;

	public SampleImageContribution() {
		super(ChooseSimulatedSampleImage.COMMAND_ID);
	}

	@Override
	public <U extends SimulatedSampleImage> String getText(TreeItemData<U> data) {
		return properties.sampleImage().toString();
	}

	@Override
	public <U extends SimulatedSampleImage> String getSupplementalText(TreeItemData<U> data) {
		return Objects.toString(data.data());
	}

	@Override
	public <U extends SimulatedSampleImage> Node configureCell(TreeItemData<U> data, Node content) {
		content = PseudoClassTreeCellContribution.super.configureCell(data, content);
		return super.configureCell(data, content);
	}
}