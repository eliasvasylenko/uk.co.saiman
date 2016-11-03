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
package uk.co.saiman.simulation.instrument.impl;

import java.util.Random;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SparseSampledContinuousFunction;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.simulation.instrument.SimulatedSample;

/**
 * A simulation of an acquisition data signal from a TDC.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class TDCSimulation implements DetectorSimulation {
	private static final int MAXIMUM_HITS = 10;
	private final int[] hitIndices = new int[MAXIMUM_HITS];
	private final double[] hitIntensities = new double[MAXIMUM_HITS];

	@Override
	public SampledContinuousFunction<Time, Dimensionless> acquire(Unit<Dimensionless> intensityUnits,
			Unit<Time> timeUnits, Random random, double resolution, int depth, SimulatedSample sample) {
		int hits = random.nextInt(MAXIMUM_HITS);

		/*
		 * TODO distribute "hits" number of hits
		 */

		return new SparseSampledContinuousFunction<>(timeUnits, intensityUnits, 1 / resolution, depth, hits, hitIndices,
				hitIntensities);
	}
}
