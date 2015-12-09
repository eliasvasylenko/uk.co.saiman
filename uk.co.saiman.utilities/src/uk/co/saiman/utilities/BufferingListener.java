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
