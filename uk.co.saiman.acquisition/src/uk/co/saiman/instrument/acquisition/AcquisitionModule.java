/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.function.Consumer;

import uk.co.saiman.data.SampledContinuum;
import uk.co.saiman.instrument.HardwareModule;

public interface AcquisitionModule extends HardwareModule {
	void startAcquisition();

	void stopAcquisition();

	@Override
	default void abortOperation() {
		stopAcquisition();
	}

	@Override
	default void reset() {
		abortOperation();
	}

	boolean isAcquiring();

	/**
	 * Get the last acquired acquisition data. This leaves the format of the
	 * acquired data to the discretion of the implementing hardware module.
	 * 
	 * @return
	 */
	SampledContinuum getLastAcquisitionData();

	/**
	 * Add an acquisition event listener indefinitely. The listener will not be
	 * triggered until the start of an experiment via {@link #startAcquisition()},
	 * and will be removed after the experiment is complete.
	 * 
	 * @param listener
	 *          A consumer of {@link SampledContinuum} objects which will be
	 *          passed the results of each acquisition step.
	 */
	void addSingleAcquisitionListener(Consumer<? super SampledContinuum> listener);

	/**
	 * Add an acquisition event listener indefinitely. The listener may be
	 * triggered with acquisition events that happen outside the scope of an
	 * actual acquisition experiment, in the case of an "always on" instrument
	 * setup.
	 * 
	 * @param listener
	 *          A consumer of {@link SampledContinuum} objects which will be
	 *          passed the results of each acquisition step.
	 */
	void addAcquisitionListener(Consumer<? super SampledContinuum> listener);

	/**
	 * Remove an acquisition listener which has been added via
	 * {@link #addAcquisitionListener(Consumer)} or
	 * {@link #addSingleAcquisitionListener(Consumer)}.
	 * 
	 * @param listener
	 *          The consumer to remove.
	 */
	void removeAcquisitionListener(Consumer<? super SampledContinuum> listener);

	double getAcquisitionResolution();

	void setAcquisitionTime(double time);

	double getAcquisitionTime();

	default int getAcquisitionDepth() {
		return (int) (getAcquisitionTime() / getAcquisitionResolution());
	}
}
