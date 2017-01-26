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
package uk.co.saiman.simulation.instrument.impl;

import java.util.Random;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import uk.co.saiman.data.ArraySampledContinuousFunction;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SampledDomain;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.simulation.instrument.SimulatedSample;

/**
 * A simulation of an acquisition data signal from an ADC.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = ADCSimulationConfiguration.class)
@Component(configurationPid = ADCSimulation.CONFIGURATION_PID)
public class ADCSimulation implements DetectorSimulation {
	static final String CONFIGURATION_PID = "uk.co.saiman.simulation.adc";

	private double[] intensities = new double[0];

	private final Random random = new Random();

	@Override
	public String getId() {
		return CONFIGURATION_PID;
	}

	@Override
	public SampledContinuousFunction<Time, Dimensionless> acquire(
			SampledDomain<Time> domain,
			Unit<Dimensionless> intensityUnits,
			SimulatedSample nextSample) {
		if (this.intensities.length != domain.getDepth()) {
			intensities = new double[domain.getDepth()];
		}

		double[] intensities = this.intensities;

		double scale = 0;
		double scaleDelta = 1d / domain.getDepth();

		for (int j = 0; j < intensities.length; j++) {
			scale += scaleDelta;
			intensities[j] = 0.01 + scale * (1 - scale + random.nextDouble() * Math.max(0, (int) (scale * 20) % 4 - 1));
		}

		return new ArraySampledContinuousFunction<>(domain, intensityUnits, intensities);
	}
}
