package uk.co.saiman.comms;

import org.osgi.util.function.Function;

public interface Command<T, I, O> {
	T getId();

	I invoke(O argument);

	O prototype();

	default I invoke(Function<O, O> argument) {
		return invoke(argument.apply(prototype()));
	}
}
