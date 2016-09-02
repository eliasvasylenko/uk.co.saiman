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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.sample.SampleExperimentType;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.simulation.experiment.SampleExperimentSimulationType;
import uk.co.saiman.simulation.experiment.SimulatedXYStageRasterConfiguration;
import uk.co.saiman.simulation.instrument.SimulatedSampleImageDevice;

/**
 * An implementation of {@link SampleExperimentType} for a simulated XY stage
 * instrument.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = { XYStageExperimentType.class, SampleExperimentType.class, SampleExperimentSimulationType.class,
		ExperimentType.class })
public class XYStageExperimentSimulationType implements XYStageExperimentType<SimulatedXYStageRasterConfiguration>,
		SampleExperimentSimulationType<SimulatedXYStageRasterConfiguration> {
	private static final String DEFAULT_NAME = "XY Sample";

	@Reference
	SimulatedSampleImageDevice rasterSimulation;

	@Reference
	Units units;

	@Override
	public SimulatedSampleImageDevice device() {
		return rasterSimulation;
	}

	@Override
	public SimulatedXYStageRasterConfiguration createState(
			ExperimentNode<?, ? extends SimulatedXYStageRasterConfiguration> forNode) {
		SimulatedXYStageRasterConfiguration configuration = new SimulatedXYStageRasterConfiguration(units);

		configuration.setName(DEFAULT_NAME);

		configuration.setX(units.metre().milli().getQuantity(0));
		configuration.setY(units.metre().milli().getQuantity(0));

		return configuration;
	}

	@Override
	public void execute(ExperimentNode<?, ? extends SimulatedXYStageRasterConfiguration> node) {
		XYStageExperimentType.super.execute(node);

		rasterSimulation.setSampleImage(node.getState().getSampleImage());

		rasterSimulation.setRedChemical(node.getState().getRedChemical());
		rasterSimulation.setGreenChemical(node.getState().getGreenChemical());
		rasterSimulation.setBlueChemical(node.getState().getBlueChemical());
	}
}
