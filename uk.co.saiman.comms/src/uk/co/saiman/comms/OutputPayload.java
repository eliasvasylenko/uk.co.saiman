package uk.co.saiman.comms;

public interface OutputPayload<T> {
	Class<T> objectClass();

	byte[] objectToBytes(T object);

	static OutputPayload<Void> voidOutput() {
		return new OutputPayload<Void>() {
			@Override
			public Class<Void> objectClass() {
				return Void.class;
			}

			@Override
			public byte[] objectToBytes(Void object) {
				return new byte[] {};
			}
		};
	}
}
