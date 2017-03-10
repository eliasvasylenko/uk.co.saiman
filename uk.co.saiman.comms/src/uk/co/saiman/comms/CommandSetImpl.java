package uk.co.saiman.comms;

import static java.nio.ByteBuffer.allocate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A simple immutable class defining named addresses for pushing and requesting
 * bytes to and from a {@link CommsChannel comms channel}.
 * 
 * <p>
 * A command definition comprises of a {@link CommandId command id} followed by
 * a number of input and output bytes. The {@link CommandId#getBytes() command
 * id bytes} are sent down the comms channel, then a number of further bytes are
 * sent and received through the channel according to the shape of the command.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          The command identifier type. This may typically consist of an
 *          address and an operation type.
 */
public abstract class CommandSetImpl<T extends CommandId> implements CommandSet<T> {
	private final String name;
	private final Class<T> idClass;
	private final Map<T, CommandDefinition<T, ?, ?>> commands;

	private CommsChannel commsChannel;
	private ByteChannel byteChannel;

	private final Object commsChannelLock = new Object();

	/**
	 * Initialize an empty address space.
	 */
	public CommandSetImpl(String name, Class<T> idClass) {
		this.name = name;
		this.idClass = idClass;
		this.commands = new HashMap<>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<T> getCommandIdClass() {
		return idClass;
	}

	protected void setChannel(CommsChannel channel) {
		synchronized (commsChannelLock) {
			commsChannel = channel;
		}
	}

	@Override
	public CommsChannel getChannel() {
		return commsChannel;
	}

	protected synchronized void useChannel(Consumer<ByteChannel> action) {
		/*
		 * TODO timeout byte channel being open ...
		 */
		try (ByteChannel byteChannel = getChannel().openChannel()) {
			action.accept(byteChannel);
		} catch (IOException e) {
			throw new CommsException("Problem opening comms channel");
		}
	}

	protected synchronized <T> T useChannel(Function<ByteChannel, T> action) {
		try (ByteChannel byteChannel = getChannel().openChannel()) {
			return action.apply(byteChannel);
		} catch (IOException e) {
			throw new CommsException("Problem opening comms channel");
		}
	}

	public <I, O> CommandDefinition<T, I, O> addCommand(T id, InputPayload<I> input, OutputPayload<O> output) {
		CommandDefinition<T, I, O> command = new CommandDefinition<>(this, id, input, output);
		commands.put(id, command);

		return command;
	}

	@Override
	public Stream<T> getCommands() {
		return commands.keySet().stream();
	}

	@Override
	public CommandDefinition<T, ?, ?> getCommand(T id) {
		CommandDefinition<T, ?, ?> command = commands.get(id);

		if (command == null) {
			throw new CommsException("Command undefined " + id);
		}

		return command;
	}

	void sendBytes(ByteBuffer outputBuffer) {
		useChannel(channel -> {
			try {
				channel.write(outputBuffer);
			} catch (IOException e) {
				throw new CommsException("Problem sending output bytes");
			}
		});
	}

	ByteBuffer receiveBytes(int expectedBytes) {
		return useChannel(channel -> {
			ByteBuffer inputBuffer = allocate(expectedBytes);
			int read;

			try {
				read = channel.read(inputBuffer);
			} catch (IOException e) {
				throw new CommsException("Problem receiving input bytes");
			}

			if (read != expectedBytes) {
				throw new CommsException("Input byte count does not match expected");
			}

			return inputBuffer;
		});
	}
}
