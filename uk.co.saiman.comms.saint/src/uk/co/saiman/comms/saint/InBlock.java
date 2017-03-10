package uk.co.saiman.comms.saint;

import java.util.function.Supplier;

public interface InBlock<T> {
	T getActual();

	static <T> InBlock<T> inBlock(Supplier<T> getActual) {
		return getActual::get;
	}
}
