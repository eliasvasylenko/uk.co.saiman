package uk.co.saiman.comms.saint;

import java.util.function.Function;

import uk.co.saiman.comms.OutputPayload;

public class SaintOutputPayload<T> implements OutputPayload<T> {
	private static final SaintOutputPayload<Void> VOID_OUTPUT = new SaintOutputPayload<>(Void.class, o -> (byte) 0);

	private final Function<T, Byte> objectToByte;
	private final Class<T> objectClass;

	public SaintOutputPayload(Class<T> objectClass, Function<T, Byte> objectToByte) {
		this.objectClass = objectClass;
		this.objectToByte = objectToByte;
	}

	@Override
	public Class<T> objectClass() {
		return objectClass;
	}

	@Override
	public byte[] objectToBytes(T object) {
		byte[] bytes = new byte[2];
		bytes[1] = objectToByte.apply(object);

		return bytes;
	}

	public static OutputPayload<Void> voidOutput() {
		return VOID_OUTPUT;
	}
}
