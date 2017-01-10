/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex.treecontributions;

import static java.util.stream.Stream.of;
import static uk.co.strangeskies.reflection.token.TypedObject.typedObject;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.experiment.SimulatedSampleImageConfiguration;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.strangeskies.eclipse.EclipseTreeContribution;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeChildContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;
import uk.co.strangeskies.reflection.token.TypedObject;

@SuppressWarnings("javadoc")
@Component(service = EclipseTreeContribution.class, scope = ServiceScope.PROTOTYPE)
public class SampleImageConfigurationContribution implements EclipseTreeContribution<SimulatedSampleImageConfiguration>,
		TreeChildContribution<SimulatedSampleImageConfiguration>, TreeTextContribution<SimulatedSampleImageConfiguration>,
		PseudoClassTreeCellContribution<SimulatedSampleImageConfiguration> {
	@Inject
	@Localize
	SimulationProperties properties;

	@Override
	public <U extends SimulatedSampleImageConfiguration> boolean hasChildren(TreeItemData<U> data) {
		return true;
	}

	@Override
	public <U extends SimulatedSampleImageConfiguration> Stream<TypedObject<?>> getChildren(TreeItemData<U> data) {
		return of(
				typedObject(data.data().getSampleImage(), SimulatedSampleImage.class),
				typedObject(
						new ChemicalColor(properties.redChemical(), data.data().getRedChemical(), data.data()::setRedChemical),
						ChemicalColor.class),
				typedObject(
						new ChemicalColor(
								properties.greenChemical(),
								data.data().getGreenChemical(),
								data.data()::setGreenChemical),
						ChemicalColor.class),
				typedObject(
						new ChemicalColor(properties.blueChemical(), data.data().getBlueChemical(), data.data()::setBlueChemical),
						ChemicalColor.class));
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
