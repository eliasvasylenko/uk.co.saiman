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

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.sample.SampleExperimentType;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.simulation.experiment.SampleExperimentSimulationType;
import uk.co.saiman.simulation.experiment.XYStageSimulationConfiguration;
import uk.co.saiman.simulation.instrument.ImageSampleDeviceSimulation;

/**
 * An implementation of {@link SampleExperimentType} for a simulated XY stage
 * instrument.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = { XYStageExperimentType.class, SampleExperimentType.class, SampleExperimentSimulationType.class,
		ExperimentType.class })
public class XYStageExperimentSimulationType implements XYStageExperimentType<XYStageSimulationConfiguration>,
		SampleExperimentSimulationType<XYStageSimulationConfiguration> {
	@Reference
	ImageSampleDeviceSimulation rasterSimulation;

	@Reference
	Units units;

	@Override
	public ImageSampleDeviceSimulation device() {
		return rasterSimulation;
	}

	@Override
	public XYStageSimulationConfiguration createState(
			ExperimentNode<?, ? extends XYStageSimulationConfiguration> forNode) {
		XYStageSimulationConfiguration configuration = new XYStageSimulationConfiguration();

		Quantity<Length> home = units.metre().milli().getQuantity(0);

		configuration.setX(home);
		configuration.setY(home);

		return configuration;
	}

	@Override
	public void execute(ExperimentNode<?, ? extends XYStageSimulationConfiguration> node) {
		XYStageExperimentType.super.execute(node);

		rasterSimulation.setSampleImage(node.getState().getSampleImage());

		rasterSimulation.setRedChemical(node.getState().getRedChemical());
		rasterSimulation.setGreenChemical(node.getState().getGreenChemical());
		rasterSimulation.setBlueChemical(node.getState().getBlueChemical());
	}
}
