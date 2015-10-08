/*
 * Copyright (C) 2015 Elias N Vasylenko <eliasvasylenko@gmail.com>
 *
 * This file is part of uk.co.saiman.msapex.instrument.api.
 *
 * uk.co.saiman.msapex.instrument.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with uk.co.saiman.msapex.instrument.api.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument;

import java.util.Set;

public interface Instrument {
	/**
	 * An instrument has a 1 to 1 relationship with a lifecycle state. When
	 * transitions between states are requested of an instrument, the action is
	 * delegated to lifecycle participants registered with that instrument.
	 * 
	 * @author Elias N Vasylenko
	 *
	 */
	public enum InstrumentLifecycleState {
		/**
		 * Instrument is in idle state.
		 */
		STANDBY,
		/**
		 * Make sure vacuum is ready and ramp up voltages, etc.
		 */
		BEGIN_OPERATION,
		/**
		 * Whilst operating, experiments may be processed.
		 */
		OPERATING,
		/**
		 * Ramp down voltages, disengage any operating hardware, etc.
		 */
		END_OPERATION
	}

	boolean operate();

	void standby();

	InstrumentLifecycleState state();

	void registerLifecycleParticipant(InstrumentLifecycleParticipant participant);

	void unregisterLifecycleParticipant(InstrumentLifecycleParticipant participant);

	Set<InstrumentModule<?>> getModules();

	<T extends InstrumentModule<?>> T getExactModule(Class<T> moduleClass);

	<T extends InstrumentModule<?>> Set<T> getModules(Class<T> moduleClass);

	boolean hasModule(Class<? extends InstrumentModule<?>> moduleClass);
}
