/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
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

import java.lang.reflect.Type;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.ExperimentConfigurationContext;
import uk.co.saiman.experiment.ExperimentExecutionContext;
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
	SimulatedSampleImageDevice stageSimulation;

	@Reference
	Units units;

	@Override
	public SimulatedSampleImageDevice device() {
		return stageSimulation;
	}

	@Override
	public SimulatedXYStageRasterConfiguration createState(
			ExperimentConfigurationContext<SimulatedXYStageRasterConfiguration> forNode) {
		SimulatedXYStageRasterConfiguration configuration = new SimulatedXYStageRasterConfiguration(
				forNode,
				stageSimulation,
				units);

		configuration.setName(DEFAULT_NAME);

		configuration.setX(units.metre().milli().getQuantity(0));
		configuration.setY(units.metre().milli().getQuantity(0));

		return configuration;
	}

	@Override
	public void execute(ExperimentExecutionContext<SimulatedXYStageRasterConfiguration> context) {
		XYStageExperimentType.super.execute(context);

		stageSimulation.setSampleImage(context.node().getState().getSampleImage());

		stageSimulation.setRedChemical(context.node().getState().getRedChemical().composition());
		stageSimulation.setGreenChemical(context.node().getState().getGreenChemical().composition());
		stageSimulation.setBlueChemical(context.node().getState().getBlueChemical().composition());
	}

	@Override
	public Type getThisType() {
		return XYStageExperimentSimulationType.class;
	}
}
