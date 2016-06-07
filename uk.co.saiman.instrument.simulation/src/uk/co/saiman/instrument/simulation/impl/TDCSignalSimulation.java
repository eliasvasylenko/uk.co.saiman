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

import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SparseSampledContinuousFunction;
import uk.co.saiman.instrument.simulation.SignalSimulation;
import uk.co.saiman.instrument.simulation.SimulationSample;

/**
 * A configurable software simulation of an acquisition hardware module.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class TDCSignalSimulation implements SignalSimulation {
	private static final int MAXIMUM_HITS = 10;
	private final int[] hitIndices = new int[MAXIMUM_HITS];
	private final double[] hitIntensities = new double[MAXIMUM_HITS];

	@Override
	public SampledContinuousFunction acquire(Random random, double resolution, int depth, SimulationSample sample) {
		int hits = random.nextInt(MAXIMUM_HITS);

		/*
		 * TODO distribute "hits" number of hits
		 */

		return new SparseSampledContinuousFunction(1 / resolution, depth, hits, hitIndices, hitIntensities);
	}
}
