/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Component;

import javafx.scene.Node;
import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.experiment.SimulatedSampleImageConfiguration;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.strangeskies.eclipse.CommandTreeCellContribution;
import uk.co.strangeskies.eclipse.EclipseTreeContribution;
import uk.co.strangeskies.eclipse.EclipseTreeContributionImpl;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeCellContribution;
import uk.co.strangeskies.fx.TreeChildContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;
import uk.co.strangeskies.reflection.TypeToken;
import uk.co.strangeskies.reflection.TypedObject;
import uk.co.strangeskies.text.properties.Localized;

/**
 * An implementation of {@link TreeCellContribution} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = EclipseTreeContribution.class)
public class SimulatedSampleExperimentNodeContributor extends EclipseTreeContributionImpl {
	@SuppressWarnings("javadoc")
	public SimulatedSampleExperimentNodeContributor() {
		super(SampleExperimentNodeContribution.class, SampleImageConfigurationContribution.class,
				SampleImageContribution.class, ChemicalContribution.class);
	}
}

class SampleExperimentNodeContribution
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

class ChemicalColor {
	private final Localized<String> name;
	private ChemicalComposition chemical;
	private final Consumer<ChemicalComposition> setChemical;

	public ChemicalColor(Localized<String> name, ChemicalComposition chemical,
			Consumer<ChemicalComposition> setChemical) {
		this.name = name;
		this.chemical = chemical;
		this.setChemical = setChemical;
	}

	public Localized<String> getName() {
		return name;
	}

	public ChemicalComposition getChemical() {
		return chemical;
	}

	public void getChemical(ChemicalComposition chemical) {
		setChemical.accept(chemical);
		this.chemical = chemical;
	}
}

class SampleImageConfigurationContribution implements TreeChildContribution<SimulatedSampleImageConfiguration>,
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

class SampleImageContribution extends CommandTreeCellContribution<SimulatedSampleImage>
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
		return PseudoClassTreeCellContribution.super.configureCell(data, super.configureCell(data, content));
	}
}

class ChemicalContribution
		implements PseudoClassTreeCellContribution<ChemicalColor>, TreeTextContribution<ChemicalColor> {
	@Override
	public <U extends ChemicalColor> String getText(TreeItemData<U> data) {
		return data.data().getName().toString();
	}

	@Override
	public <U extends ChemicalColor> String getSupplementalText(TreeItemData<U> data) {
		return Objects.toString(data.data().getChemical());
	}
}
