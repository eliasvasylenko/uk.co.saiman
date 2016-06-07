/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.acquisition.
 *
 * uk.co.saiman.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.simulation.impl;

import java.util.Random;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.ArrayRegularSampledContinuousFunction;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.instrument.simulation.SignalSimulation;
import uk.co.saiman.instrument.simulation.SimulationSample;

/**
 * A configurable software simulation of an acquisition hardware module.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class ADCSignalSimulation implements SignalSimulation {
	private double[] intensities = new double[0];

	@Override
	public SampledContinuousFunction acquire(Random random, double resolution, int depth, SimulationSample sample) {
		if (this.intensities.length != depth) {
			intensities = new double[depth];
		}

		double[] intensities = this.intensities;

		double scale = 0;
		double scaleDelta = 1d / depth;
		for (int j = 0; j < intensities.length; j++) {
			intensities[j] = 0.5
					+ (scale += scaleDelta) * (1 - scale + random.nextDouble() * Math.max(0, (int) (scale * 20) % 4 - 1)) * 20;
		}

		return new ArrayRegularSampledContinuousFunction(1 / (resolution * 1_000), 0, intensities);
	}
}
