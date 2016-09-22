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
package uk.co.saiman.simulation.msapex;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.simulation.msapex.treecontributions.ChemicalContribution;
import uk.co.saiman.simulation.msapex.treecontributions.SampleExperimentNodeContribution;
import uk.co.saiman.simulation.msapex.treecontributions.SampleImageConfigurationContribution;
import uk.co.saiman.simulation.msapex.treecontributions.SampleImageContribution;
import uk.co.strangeskies.eclipse.EclipseModularTreeContributor;
import uk.co.strangeskies.eclipse.EclipseModularTreeContributorImpl;
import uk.co.strangeskies.fx.TreeCellContribution;

/**
 * An implementation of {@link TreeCellContribution} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = EclipseModularTreeContributor.class)
public class SimulatedSampleExperimentNodeContributor extends EclipseModularTreeContributorImpl {
	@SuppressWarnings("javadoc")
	public SimulatedSampleExperimentNodeContributor() {
		super(SampleExperimentNodeContribution.class, SampleImageConfigurationContribution.class,
				SampleImageContribution.class, ChemicalContribution.class);
	}
}
