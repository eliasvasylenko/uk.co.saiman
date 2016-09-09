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
package uk.co.saiman.simulation.experiment.impl;

import static java.util.Collections.synchronizedSet;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumConfiguration;
import uk.co.saiman.experiment.spectrum.SpectrumExperimentType;
import uk.co.saiman.simulation.experiment.SampleExperimentSimulationType;
import uk.co.saiman.simulation.instrument.SimulatedAcquisitionDevice;
import uk.co.saiman.simulation.instrument.SimulatedSampleDevice;

@Component(service = { ExperimentType.class, SpectrumExperimentType.class })
public class SpectrumExperimentSimulationType implements SpectrumExperimentType<SpectrumConfiguration> {
	final Set<SampleExperimentSimulationType<?>> sampleExperiments = synchronizedSet(new HashSet<>());
	@Reference
	SimulatedAcquisitionDevice acquisitionSimulation;

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void addSampleExperiment(SampleExperimentSimulationType<?> sampleExperiment) {
		sampleExperiments.add(sampleExperiment);
	}

	public void removeSampleExperiment(SampleExperimentSimulationType<?> sampleExperiment) {
		sampleExperiments.remove(sampleExperiment);
	}

	@Override
	public SpectrumConfiguration createState(ExperimentNode<?, ? extends SpectrumConfiguration> forNode) {
		return new SpectrumConfiguration() {
			@Override
			public String getSpectrumName() {
				return "Untitled Spectrum";
			}
		};
	}

	@Override
	public void execute(ExperimentNode<?, ? extends SpectrumConfiguration> node) {
		SimulatedSampleDevice sample = node.getAncestor(sampleExperiments).get().getType().device();
		acquisitionSimulation.setSample(sample);

		acquisitionSimulation.startAcquisition();
	}

	@Override
	public boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
		return parentNode.getAncestor(sampleExperiments).isPresent();
	}

	@Override
	public boolean mayComeBefore(ExperimentNode<?, ?> penultimateDescendantNode, ExperimentType<?> descendantNodeType) {
		return descendantNodeType != this;
	}
}
