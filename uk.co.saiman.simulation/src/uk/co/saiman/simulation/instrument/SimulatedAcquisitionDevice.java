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
package uk.co.saiman.simulation.instrument;

import java.util.Set;

import uk.co.saiman.acquisition.AcquisitionDevice;

public interface SimulatedAcquisitionDevice extends AcquisitionDevice, SimulatedDevice {
	/**
	 * @return the signal detector simulations available for use
	 */
	public Set<DetectorSimulation> getDetectors();

	/**
	 * @return the signal detector simulation currently in use
	 */
	public DetectorSimulation getDetector();

	/**
	 * @param detector
	 *          the new signal detector simulation to use
	 */
	public void setDetector(DetectorSimulation detector);

	/**
	 * @return the sample device simulations available for use
	 */
	public Set<SimulatedSampleDevice> getSamples();

	/**
	 * @return the sample device simulation currently in use
	 */
	public SimulatedSampleDevice getSample();

	/**
	 * @param sample
	 *          the new sample device simulation to use
	 */
	public void setSample(SimulatedSampleDevice sample);
}
