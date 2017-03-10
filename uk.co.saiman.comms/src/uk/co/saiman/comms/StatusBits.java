package uk.co.saiman.comms;

import java.util.BitSet;

public abstract class StatusBits<T extends Enum<T>> {
	private final BitSet bits;

	public StatusBits(Class<T> enumClass) {
		this.bits = new BitSet(enumClass.getEnumConstants().length);
	}

	public StatusBits(Class<T> enumClass, byte[] bytes) {
		throw new UnsupportedOperationException();
	}

	public boolean isSet(T t) {
		return bits.get(t.ordinal());
	}

	protected StatusBits<T> set(T bit, boolean set) {
		bits.set(bit.ordinal(), set);
		return this;
	}

	public abstract StatusBits<T> withSet(T t, boolean set);

	public byte[] getBytes() {
		throw new UnsupportedOperationException();
	}
}
