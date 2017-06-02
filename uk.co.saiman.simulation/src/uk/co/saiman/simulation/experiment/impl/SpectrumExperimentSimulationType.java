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
package uk.co.saiman.simulation.experiment.impl;

import static java.util.Collections.synchronizedSet;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.experiment.ExperimentConfigurationContext;
import uk.co.saiman.experiment.ExperimentExecutionContext;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumConfiguration;
import uk.co.saiman.experiment.spectrum.SpectrumExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumProperties;
import uk.co.saiman.simulation.experiment.SampleExperimentSimulationType;
import uk.co.saiman.simulation.instrument.SimulatedAcquisitionDevice;
import uk.co.saiman.simulation.instrument.SimulatedSampleDevice;
import uk.co.strangeskies.text.properties.PropertyLoader;

@Component(service = { ExperimentType.class, SpectrumExperimentType.class })
public class SpectrumExperimentSimulationType
		extends SpectrumExperimentType<SpectrumConfiguration> {
	private static final String ID = "uk.co.saiman.simulation.spectrum";

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

	@Reference
	public void setPropertyLoader(PropertyLoader propertyLoader) {
		setProperties(propertyLoader.getProperties(SpectrumProperties.class));
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public SimulatedAcquisitionDevice getAcquisitionDevice() {
		return acquisitionSimulation;
	}

	@Override
	public SpectrumConfiguration createState(
			ExperimentConfigurationContext<SpectrumConfiguration> forNode) {
		SpectrumConfiguration configuration = new SpectrumConfiguration() {
			private String name;

			@Override
			public String getSpectrumName() {
				return name;
			}

			@Override
			public void setSpectrumName(String name) {
				forNode.setID(getName() + " - " + name);
				this.name = name;
			}

			@Override
			public AcquisitionDevice getAcquisitionDevice() {
				return acquisitionSimulation;
			}
		};

		configuration.setSpectrumName(getProperties().defaultSpectrumName().toString());

		return configuration;
	}

	@Override
	public void execute(ExperimentExecutionContext<SpectrumConfiguration> context) {
		SimulatedSampleDevice sample = context
				.node()
				.getAncestor(sampleExperiments)
				.get()
				.getType()
				.device();
		acquisitionSimulation.setSample(sample);

		super.execute(context);
	}

	@Override
	public boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
		return parentNode.getAncestor(sampleExperiments).isPresent();
	}

	@Override
	public boolean mayComeBefore(
			ExperimentNode<?, ?> penultimateDescendantNode,
			ExperimentType<?> descendantNodeType) {
		return descendantNodeType != this;
	}

	@Override
	public Type getThisType() {
		return SpectrumExperimentSimulationType.class;
	}
}
