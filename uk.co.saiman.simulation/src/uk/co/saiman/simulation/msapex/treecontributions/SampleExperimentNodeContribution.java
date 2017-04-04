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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.simulation.experiment.SimulatedSampleImageConfiguration;
import uk.co.strangeskies.eclipse.EclipseTreeContribution;
import uk.co.strangeskies.fx.TreeChildContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.reflection.token.TypedObject;

@SuppressWarnings("javadoc")
@Component(service = EclipseTreeContribution.class, scope = ServiceScope.PROTOTYPE)
public class SampleExperimentNodeContribution implements
		EclipseTreeContribution<ExperimentNode<?, ? extends SimulatedSampleImageConfiguration>>,
		TreeChildContribution<ExperimentNode<?, ? extends SimulatedSampleImageConfiguration>> {
	@Override
	public <U extends ExperimentNode<?, ? extends SimulatedSampleImageConfiguration>> boolean hasChildren(
			TreeItemData<U> data) {
		return true;
	}

	@Override
	public <U extends ExperimentNode<?, ? extends SimulatedSampleImageConfiguration>> Stream<TypedObject<?>> getChildren(
			TreeItemData<U> data) {
		return of(typedObject(SimulatedSampleImageConfiguration.class, data.data().getState()));
	}
}
