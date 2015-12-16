/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class BufferingListener<T> implements Consumer<T> {
	private final Deque<T> buffer;
	private final Set<Consumer<? super T>> listeners;
	private boolean disposed;

	public BufferingListener() {
		buffer = new ArrayDeque<>();
		listeners = new HashSet<>();
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
					for (Consumer<? super T> listener : listeners) {
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

	public void dispose() {
		synchronized (buffer) {
			disposed = true;
			buffer.notifyAll();
		}
	}

	@Override
	public void accept(T item) {
		synchronized (buffer) {
			buffer.add(item);
			buffer.notifyAll();
		}
	}

	public void addListener(Consumer<? super T> listener) {
		listeners.add(listener);
	}

	public void removeListener(Consumer<? super T> listener) {
		listeners.remove(listener);
	}

	public Set<Consumer<? super T>> getListeners() {
		return new HashSet<>(listeners);
	}

	public void removeAllListeners() {
		listeners.clear();
	}
}
