package uk.co.saiman.comms;

public interface InputPayload<T> {
	Class<T> objectClass();

	T bytesToObject(byte[] bytes);

	int expectedByteCount();

	static InputPayload<Void> voidInput() {
		return new InputPayload<Void>() {
			@Override
			public Class<Void> objectClass() {
				return Void.class;
			}

			@Override
			public Void bytesToObject(byte[] bytes) {
				return null;
			}

			@Override
			public int expectedByteCount() {
				return 0;
			}
		};
	}
}
