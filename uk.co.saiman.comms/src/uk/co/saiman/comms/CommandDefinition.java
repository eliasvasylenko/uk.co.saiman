package uk.co.saiman.comms;

import static java.nio.ByteBuffer.allocateDirect;

import java.nio.ByteBuffer;

public class CommandDefinition<T extends CommandId, I, O> {
	private final CommandSetImpl<T> commandSpace;
	private final T id;
	private final InputPayload<I> input;
	private final OutputPayload<O> output;

	protected CommandDefinition(CommandSetImpl<T> commandSpace, T id, InputPayload<I> input, OutputPayload<O> output) {
		this.commandSpace = commandSpace;

		this.id = id;
		this.input = input;
		this.output = output;
	}

	public T getId() {
		return id;
	}

	public InputPayload<I> getInput() {
		return input;
	}

	public OutputPayload<O> getOutput() {
		return output;
	}

	@SuppressWarnings("unchecked")
	<C> CommandDefinition<T, C, O> withInputClass(Class<C> inputClass) {
		if (!input.objectClass().isAssignableFrom(inputClass)) {
			throw new CommsException("Input class " + inputClass + " incompatible with " + input.objectClass());
		}
		return (CommandDefinition<T, C, O>) this;
	}

	@SuppressWarnings("unchecked")
	<C> CommandDefinition<T, I, C> withOutputClass(Class<C> outputClass) {
		if (!output.objectClass().isAssignableFrom(outputClass)) {
			throw new CommsException("Output class " + outputClass + " incompatible with " + output.objectClass());
		}
		return (CommandDefinition<T, I, C>) this;
	}

	public I invoke(O argument) {
		byte[] idBytes = id.getBytes();
		byte[] outputBytes = output.objectToBytes(argument);

		ByteBuffer outputBuffer = allocateDirect(idBytes.length + outputBytes.length);
		outputBuffer.put(idBytes).put(outputBytes).flip();
		commandSpace.sendBytes(outputBuffer);

		ByteBuffer inputBuffer = commandSpace.receiveBytes(input.expectedByteCount());

		return input.bytesToObject(inputBuffer.array());
	}
}
