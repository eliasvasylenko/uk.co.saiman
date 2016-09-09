/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.sample.SampleConfiguration;
import uk.co.saiman.experiment.sample.SampleExperimentType;
import uk.co.strangeskies.eclipse.EclipseModularTreeContributor;
import uk.co.strangeskies.eclipse.EclipseModularTreeContributorImpl;
import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeCellContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;

/**
 * An implementation of {@link TreeCellContribution} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = EclipseModularTreeContributor.class)
public class SampleExperimentNodeContributor extends EclipseModularTreeContributorImpl {
	@SuppressWarnings("javadoc")
	public SampleExperimentNodeContributor() {
		super(SampleExperimentNodeContribution.class);
	}
}

class SampleExperimentNodeContribution
		implements TreeTextContribution<ExperimentNode<? extends SampleExperimentType<?>, ? extends SampleConfiguration>>,
		PseudoClassTreeCellContribution<ExperimentNode<? extends SampleExperimentType<?>, ? extends SampleConfiguration>> {
	@Override
	public <U extends ExperimentNode<? extends SampleExperimentType<?>, ? extends SampleConfiguration>> String getText(
			TreeItemData<U> data) {
		return data.data().getState().getName();
	}

	@Override
	public <U extends ExperimentNode<? extends SampleExperimentType<?>, ? extends SampleConfiguration>> String getSupplementalText(
			TreeItemData<U> data) {
		return data.data().getState().toString();
	}
}
