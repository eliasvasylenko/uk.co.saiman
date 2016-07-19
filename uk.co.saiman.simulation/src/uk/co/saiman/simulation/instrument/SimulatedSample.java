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
package uk.co.saiman.simulation.instrument;

import java.util.Map;

import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.simulation.instrument.impl.AcquisitionSimulationDeviceImpl;

/**
 * A description of a simulated physical sample, which can be turned into a
 * simulated acquisition signal by way of a {@link DetectorSimulation} and an
 * {@link AcquisitionSimulationDeviceImpl}.
 * 
 * @author Elias N Vasylenko
 */
public interface SimulatedSample {
	/**
	 * @return a mapping from chemicals which constitute the sample to their
	 *         intensities
	 */
	Map<ChemicalComposition, Double> chemicalIntensities();
}
