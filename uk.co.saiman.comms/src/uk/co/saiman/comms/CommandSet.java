package uk.co.saiman.comms;

import java.util.stream.Stream;

public interface CommandSet<T> {
	String getName();

	boolean isOpen();

	boolean open();

	boolean close();

	Class<T> getCommandIdClass();

	Stream<T> getCommands();

	Command<T, ?, ?> getCommand(T id);

	Comms getChannel();
}
