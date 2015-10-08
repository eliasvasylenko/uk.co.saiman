/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.InstrumentLifecycleParticipant;
import uk.co.saiman.instrument.InstrumentModule;
import uk.co.strangeskies.utilities.IdentityProperty;

@Component(service = Instrument.class)
public class InstrumentImpl implements Instrument {
	private InstrumentLifecycleState state;
	private final Set<InstrumentLifecycleParticipant> participants;

	private final Map<Class<?>, InstrumentModule<?>> modules;

	public InstrumentImpl() {
		state = InstrumentLifecycleState.STANDBY;
		participants = new HashSet<>();

		modules = new HashMap<>();
	}

	@Override
	public InstrumentLifecycleState state() {
		return state;
	}

	@Override
	@Reference
	public void registerLifecycleParticipant(InstrumentLifecycleParticipant participant) {
		participants.add(participant);
	}

	@Override
	public void unregisterLifecycleParticipant(InstrumentLifecycleParticipant participant) {
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
								transitionToState(InstrumentLifecycleState.STANDBY);
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

	@Reference
	public void addModule(InstrumentModule<?> module) {
		modules.putIfAbsent(module.getClass(), module);
	}

	public void removeModule(InstrumentModule<?> module) {
		modules.remove(module.getClass(), module);
	}

	@Override
	public Set<InstrumentModule<?>> getModules() {
		return new HashSet<>(modules.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InstrumentModule<?>> T getExactModule(Class<T> moduleType) {
		return (T) modules.get(moduleType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends InstrumentModule<?>> Set<T> getModules(Class<T> moduleClass) {
		return modules.keySet().stream().filter(moduleClass::isAssignableFrom).map(c -> (T) modules.get(c))
				.collect(Collectors.toSet());
	}

	@Override
	public boolean hasModule(Class<? extends InstrumentModule<?>> moduleClass) {
		return modules.containsKey(moduleClass);
	}
}
