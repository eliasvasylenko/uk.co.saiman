package uk.co.saiman.comms.saint;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface OutBlock<T> {
	T getRequested();

	void request(T data);

	default void modifyRequest(Function<T, T> data) {
		request(data.apply(getRequested()));
	}

	static <T> OutBlock<T> outBlock(Consumer<T> request, Supplier<T> getRequested) {
		return new OutBlock<T>() {
			@Override
			public void request(T data) {
				request.accept(data);
			}

			@Override
			public T getRequested() {
				return getRequested.get();
			}

		};
	}

	static <T> OutBlock<T> bufferedOutBlock(Consumer<T> request, Supplier<T> getRequested) {
		return new OutBlock<T>() {
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
		};
	}
}
