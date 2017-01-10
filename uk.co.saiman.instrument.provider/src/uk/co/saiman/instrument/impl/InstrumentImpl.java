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
 * This file is part of uk.co.saiman.instrument.provider.
 *
 * uk.co.saiman.instrument.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.impl;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.InstrumentLifecycleParticipant;
import uk.co.saiman.instrument.InstrumentLifecycleState;
import uk.co.strangeskies.utilities.IdentityProperty;

/**
 * Reference implementation of {@link Instrument}, as an OSGi service.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class InstrumentImpl implements Instrument {
	private InstrumentLifecycleState state;
	private final Set<InstrumentLifecycleParticipant> participants;

	/**
	 * Create an empty instrument in standby.
	 */
	public InstrumentImpl() {
		state = InstrumentLifecycleState.STANDBY;
		participants = new HashSet<>();
	}

	@Override
	public InstrumentLifecycleState state() {
		return state;
	}

	@Override
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	public synchronized void registerLifecycleParticipant(InstrumentLifecycleParticipant participant) {
		participants.add(participant);
		participant.initialise(this);
	}

	@Override
	public synchronized void unregisterLifecycleParticipant(InstrumentLifecycleParticipant participant) {
		participants.remove(participant);
	}

	@Override
	public synchronized boolean operate() {
		if (state == InstrumentLifecycleState.OPERATING)
			return true;
		else if (transitionToState(InstrumentLifecycleState.BEGIN_OPERATION))
			return transitionToState(InstrumentLifecycleState.OPERATING);
		else
			return false;
	}

	@Override
	public synchronized void standby() {
		if (state == InstrumentLifecycleState.OPERATING)
			if (transitionToState(InstrumentLifecycleState.END_OPERATION))
				transitionToState(InstrumentLifecycleState.STANDBY);
	}

	private synchronized boolean transitionToState(InstrumentLifecycleState state) {
		return transitionToStateImpl(state);
	}

	private boolean transitionToStateImpl(InstrumentLifecycleState state) {
		IdentityProperty<Boolean> success = new IdentityProperty<>(true);

		if (this.state == state) {
			success.set(false);
		} else {
			Set<Thread> participatingThreads = new HashSet<>();

			for (InstrumentLifecycleParticipant participant : participants) {
				Thread participatingThread = new Thread(() -> {
					try {
						participant.transition(this.state, state);
					} catch (RuntimeException exception) {
						synchronized (success) {
							if (success.get()) {
								success.set(false);
								for (Thread executingThread : participatingThreads) {
									executingThread.interrupt();
								}
								this.state = state;
								transitionToStateImpl(InstrumentLifecycleState.STANDBY);
							}
						}
					}
				});
				synchronized (success) {
					if (success.get()) {
						participatingThreads.add(participatingThread);
						participatingThread.start();
					} else {
						break;
					}
				}
			}

			for (Thread thread : participatingThreads) {
				try {
					/*
					 * TODO some sort of timeout here: If not in standby, try fall back to
					 * it. If already going to standby, more severe failure.
					 */
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			synchronized (success) {
				if (success.get()) {
					this.state = state;
				}
			}
		}

		return success.get();
	}
}
