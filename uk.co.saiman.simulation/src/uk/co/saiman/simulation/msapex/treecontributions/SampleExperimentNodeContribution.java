/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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