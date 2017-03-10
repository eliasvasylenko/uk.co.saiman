package uk.co.saiman.comms.saint;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface InOutBlock<T> extends InBlock<T>, OutBlock<T> {
	static <T> InOutBlock<T> inOutBlock(Consumer<T> request, Supplier<T> getRequested, Supplier<T> getActual) {
		return new InOutBlock<T>() {
			@Override
			public void request(T data) {
				request.accept(data);
			}

			@Override
			public T getRequested() {
				return getRequested.get();
			}

			@Override
			public T getActual() {
				return getActual.get();
			}
		};
	}

	static <T> InOutBlock<T> bufferedInOutBlock(Consumer<T> request, Supplier<T> getRequested, Supplier<T> getActual) {
		return new InOutBlock<T>() {
			private T data = getRequested.get();

			@Override
			public void request(T data) {
				if (!this.data.equals(data)) {
					this.data = data;
					request.accept(data);
				}
			}

			@Override
			public T getRequested() {
				return data;
			}

			@Override
			public T getActual() {
				return getActual.get();
			}
		};
	}
}
