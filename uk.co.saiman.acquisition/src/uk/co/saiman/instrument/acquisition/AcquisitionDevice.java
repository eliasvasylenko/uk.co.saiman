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
package uk.co.saiman.instrument.acquisition;

import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.strangeskies.utilities.Observable;

/**
 * Software module for acquisition of {@link SampledContinuousFunction} data
 * through some mechanism. Typically continuous function data may correspond to
 * a mass spectrum.
 * 
 * @author Elias N Vasylenko
 */
public interface AcquisitionDevice extends HardwareDevice {
	/**
	 * Begin an acquisition experiment with the current configuration.
	 * 
	 * @throws IllegalStateException
	 *           If acquisition is already in progress.
	 */
	void startAcquisition();

	/**
	 * Stop any acquisition experiment that may be in progress.
	 */
	void stopAcquisition();

	@Override
	default void abortOperation() {
		stopAcquisition();
	}

	@Override
	default void reset() {
		abortOperation();
	}

	/**
	 * @return True if the module is currently in acquisition, false otherwise.
	 */
	boolean isAcquiring();

	/**
	 * @return The last acquired acquisition data. This leaves the format of the
	 *         acquired data to the discretion of the implementing hardware
	 *         module.
	 */
	SampledContinuousFunction getLastAcquisitionData();

	/**
	 * Add or remove data event observers for the next acquisition experiment.
	 * <p>
	 * The observers will not be triggered until the start of an experiment via
	 * {@link #startAcquisition()}, and will be removed after the experiment is
	 * complete.
	 * 
	 * @return An observable interface for registering single acquisition data
	 *         event listeners.
	 */
	Observable<SampledContinuousFunction> singleAcquisitionDataEvents();

	/**
	 * Add or remove data event observers.
	 * <p>
	 * The observers may be triggered with data events that happen outside the
	 * scope of an actual acquisition experiment, in the case of an "always on"
	 * instrument setup.
	 * 
	 * @return An observable interface for registering data event listeners.
	 */
	Observable<SampledContinuousFunction> dataEvents();

	/**
	 * Set the total acquisition count for a single experiment.
	 * 
	 * @param count
	 *          The number of continua to acquire for a single experiment
	 */
	void setAcquisitionCount(int count);

	/**
	 * Get the total acquisition count for a single experiment.
	 * 
	 * @return The number of continua to acquire for a single experiment
	 */
	int getAcquisitionCount();

	/**
	 * Set the active sampling duration for a single data acquisition event.
	 * 
	 * @param time
	 *          The time an acquisition will last in milliseconds
	 */
	void setAcquisitionTime(double time);

	/**
	 * Get the active sampling duration for a single data acquisition event.
	 * 
	 * @return The time an acquisition will last in milliseconds
	 */
	double getAcquisitionTime();

	/**
	 * @return The number of samples in an acquired sampled continuous function
	 *         given the current acquisition time and acquisition resolution
	 *         configuration
	 */
	default int getAcquisitionDepth() {
		return (int) (getAcquisitionTime() / getAcquisitionResolution());
	}

	/**
	 * Get the time resolution between each sample in the acquired sampled
	 * continuous function.
	 * 
	 * @return The acquisition resolution in milliseconds
	 */
	double getAcquisitionResolution();
}
