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
 * This file is part of uk.co.saiman.instrument.api.
 *
 * uk.co.saiman.instrument.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument;

/**
 * Implementers should be registered for control with an instrument to
 * participate in its lifecycle.
 * 
 * @author Elias N Vasylenko
 *
 */
public interface InstrumentLifecycleParticipant {
	/**
	 * Invoked by the controlling instrument upon registration. If participants
	 * throw an exception from this invocation, the registration will fail.
	 * 
	 * @param instrument The instrument to participate with.
	 */
	public void initialise(Instrument instrument);

	/**
	 * Invoked by the controlling instrument upon transition into a different
	 * lifecycle state. If participants throw an exception from this invocation,
	 * the transition will fail. If any transition fails, the instrument will fall
	 * back to standby.
	 * 
	 * If the experiment does fail, any threads executing this transition will be
	 * interrupted. The instrument will then reinvoke this method for each
	 * participant, from the failed target back to standby. This invocation may
	 * happen asynchronously with a previous invocation, and cannot be caused to
	 * fail by throwing.
	 * 
	 * @param from Previous state
	 * @param to Next state
	 */
	public void transition(InstrumentLifecycleState from, InstrumentLifecycleState to);
}
