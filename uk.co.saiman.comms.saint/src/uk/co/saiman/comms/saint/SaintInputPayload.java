package uk.co.saiman.comms.saint;

import java.util.function.Function;

import uk.co.saiman.comms.InputPayload;

public class SaintInputPayload<T> implements InputPayload<T> {
	private static final SaintInputPayload<Void> VOID_INPUT = new SaintInputPayload<>(Void.class, b -> null);

	private final Function<Byte, T> byteToObject;
	private final Class<T> objectClass;

	public SaintInputPayload(Class<T> objectClass, Function<Byte, T> byteToObject) {
		this.objectClass = objectClass;
		this.byteToObject = byteToObject;
	}

	@Override
	public Class<T> objectClass() {
		return objectClass;
	}

	@Override
	public T bytesToObject(byte[] bytes) {
		return byteToObject.apply(bytes[0]);
	}

	@Override
	public int expectedByteCount() {
		return 4;
	}

	public static SaintInputPayload<Void> voidInput() {
		return VOID_INPUT;
	}
}
