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
package uk.co.saiman.msapex.experiment;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.spectrum.SpectrumConfiguration;
import uk.co.saiman.experiment.spectrum.SpectrumExperimentType;
import uk.co.strangeskies.fx.TreeCellContribution;
import uk.co.strangeskies.fx.TreeTextContribution;

/**
 * An implementation of {@link TreeCellContribution} which registers the
 * experiment tree pop-up menu from the experiment project model fragment.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class SpectrumExperimentNodeContributor implements ExperimentTreeContributor {
	@Override
	public Class<SpectrumExperimentNodeContribution> getContribution() {
		return SpectrumExperimentNodeContribution.class;
	}
}

class SpectrumExperimentNodeContribution implements
		TreeTextContribution<ExperimentNode<? extends SpectrumExperimentType<?>, ? extends SpectrumConfiguration>> {
	@Override
	public String getText(ExperimentNode<? extends SpectrumExperimentType<?>, ? extends SpectrumConfiguration> data) {
		return data.getType().getName();
	}

	@Override
	public String getSupplementalText(
			ExperimentNode<? extends SpectrumExperimentType<?>, ? extends SpectrumConfiguration> data) {
		return data.getState().getSpectrumName();
	}
}
