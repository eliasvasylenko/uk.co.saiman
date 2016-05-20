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
package uk.co.saiman.instrument.simulation;

import java.util.Random;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SparseSampledContinuousFunction;
import uk.co.saiman.instrument.HardwareDevice;

/**
 * A configurable software simulation of an acquisition hardware module.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class TDCSignalSimulation extends AcquisitionSimulationDevice implements AcquisitionDevice, HardwareDevice {
	private static final int MAXIMUM_HITS = 10;
	private final int[] hitIndices = new int[MAXIMUM_HITS];
	private final double[] hitIntensities = new double[MAXIMUM_HITS];

	/**
	 * Create an acquisition simulation with the default values given by:
	 * {@link #DEFAULT_ACQUISITION_RESOLUTION} and
	 * {@link #DEFAULT_ACQUISITION_TIME}.
	 */
	public TDCSignalSimulation() {
		super();
	}

	/**
	 * Create an acquisition simulation with the given acquisition resolution and
	 * acquisition time.
	 * 
	 * @param acquisitionResolution
	 *          The time resolution between each sample in the acquired data, in
	 *          milliseconds
	 * @param acquisitionTime
	 *          The active sampling duration for a single data acquisition, in
	 *          milliseconds.
	 */
	public TDCSignalSimulation(double acquisitionResolution, double acquisitionTime) {
		super(acquisitionResolution, acquisitionTime);
	}

	@Override
	public String getName() {
		return getText().tdcDeviceName().toString();
	}

	@Override
	protected SampledContinuousFunction acquireImpl(Random random, double resolution, int depth) {
		int hits = random.nextInt(MAXIMUM_HITS);

		/*
		 * TODO distribute "hits" number of hits
		 */

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new SparseSampledContinuousFunction(1 / resolution, depth, hits, hitIndices, hitIntensities);
	}
}
