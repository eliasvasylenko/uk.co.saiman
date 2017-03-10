package uk.co.saiman.comms;

import java.util.stream.Stream;

public interface CommandSet<T extends CommandId> {
	String getName();

	Class<T> getCommandIdClass();

	Stream<T> getCommands();

	CommandDefinition<T, ?, ?> getCommand(T id);

	CommsChannel getChannel();
}
