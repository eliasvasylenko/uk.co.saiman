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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SampledDomain;
import uk.co.saiman.data.SparseSampledContinuousFunction;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.simulation.instrument.SimulatedSample;

/**
 * A simulation of an acquisition data signal from a TDC.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = TDCSimulationConfiguration.class)
@Component(configurationPid = TDCSimulation.CONFIGURATION_PID)
public class TDCSimulation implements DetectorSimulation {
	static final String CONFIGURATION_PID = "uk.co.saiman.simulation.tdc";

	private int maximumHits = 10;
	private int[] hitIndices;
	private double[] hitIntensities;

	private final Random random = new Random();

	@Override
	public String getId() {
		return CONFIGURATION_PID;
	}

	@Activate
	@Modified
	void configure(TDCSimulationConfiguration configuration) {
		maximumHits = configuration.maximumHitsPerSpectrum();
	}

	private int updateMaximumHitsPerSpectrum() {
		int currentMaximumHits = hitIndices.length;

		if (currentMaximumHits != maximumHits) {
			currentMaximumHits = maximumHits;

			hitIndices = new int[currentMaximumHits];
			hitIntensities = new double[currentMaximumHits];
		}

		return currentMaximumHits;
	}

	@Override
	public SampledContinuousFunction<Time, Dimensionless> acquire(
			SampledDomain<Time> domain,
			Unit<Dimensionless> intensityUnits,
			SimulatedSample nextSample) {
		int hits = random.nextInt(updateMaximumHitsPerSpectrum());

		/*
		 * TODO distribute "hits" number of hits
		 */

		return new SparseSampledContinuousFunction<>(domain, intensityUnits, hits, hitIndices, hitIntensities);
	}
}
