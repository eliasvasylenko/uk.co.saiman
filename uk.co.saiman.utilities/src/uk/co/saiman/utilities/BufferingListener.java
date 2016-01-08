/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.utilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A buffer to decouple the delivery of events with their sequential
 * consumption, such that the event firing threads are not blocked by listeners.
 * 
 * Listeners are invoked in the order they are added.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The type of event to listen for
 */
public class BufferingListener<T> implements Consumer<T> {
	private final Deque<T> buffer;
	private final List<Consumer<? super T>> listeners;
	private boolean disposed;

	/**
	 * Initialise a buffering listener with an empty queue and an empty set of
	 * listeners.
	 */
	public BufferingListener() {
		buffer = new ArrayDeque<>();
		listeners = new ArrayList<>();
		disposed = false;

		Thread forwardThread = new Thread(() -> {
			boolean finished = false;

			do {
				T item = null;
				synchronized (buffer) {
					while (buffer.isEmpty() && !finished) {
						if (disposed) {
							finished = true;
						} else {
							try {
								buffer.wait();
							} catch (Exception e) {}
						}
					}
					item = buffer.remove();
				}

				if (item != null) {
					for (Consumer<? super T> listener : new ArrayList<>(listeners)) {
						listener.accept(item);
					}
				}
			} while (!finished);
		});
		forwardThread.setDaemon(true);
		forwardThread.start();
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			dispose();
		} finally {
			super.finalize();
		}
	}

	/**
	 * Discard the event queue and cease forwarding of events to listeners.
	 */
	public void dispose() {
		synchronized (buffer) {
			disposed = true;
			buffer.notifyAll();
		}
	}

	/**
	 * Fire an event.
	 * 
	 * @param item
	 *          The event data
	 */
	@Override
	public void accept(T item) {
		synchronized (buffer) {
			buffer.add(item);
			buffer.notifyAll();
		}
	}

	/**
	 * Add the given listener to the set.
	 * 
	 * @param listener
	 *          A listener to receive forwarded events
	 */
	public void addListener(Consumer<? super T> listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the given listener from the set.
	 * 
	 * @param listener
	 *          A listener to receive forwarded events
	 */
	public void removeListener(Consumer<? super T> listener) {
		listeners.remove(listener);
	}

	/**
	 * @return The set of all chained listeners.
	 */
	public Set<Consumer<? super T>> getListeners() {
		return new HashSet<>(listeners);
	}

	/**
	 * Clear all chained listeners.
	 */
	public void clearListeners() {
		listeners.clear();
	}
}
