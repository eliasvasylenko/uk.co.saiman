package uk.co.saiman.comms;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class NamedBits<T extends Enum<T>> {
	private final BitSet bits;
	private final Class<T> enumClass;

	public NamedBits(Class<T> enumClass) {
		this.bits = new BitSet(enumClass.getEnumConstants().length);
		this.enumClass = enumClass;
	}

	public NamedBits(Class<T> enumClass, byte[] bytes) {
		this.bits = BitSet.valueOf(bytes);
		this.enumClass = enumClass;
	}

	protected BitSet getBitSet() {
		return (BitSet) bits.clone();
	}

	public Class<T> getBitClass() {
		return enumClass;
	}

	public boolean isSet(T t) {
		return bits.get(t.ordinal());
	}

	public NamedBits<T> withSet(T bit, boolean set) {
		NamedBits<T> copy = new NamedBits<>(enumClass, getBytes());
		copy.bits.set(bit.ordinal(), set);
		return copy;
	}

	public byte[] getBytes() {
		return bits.toByteArray();
	}

	public Map<T, Boolean> toMap() {
		Map<T, Boolean> map = new LinkedHashMap<>();
		for (T t : enumClass.getEnumConstants()) {
			map.put(t, isSet(t));
		}
		return map;
	}

	@Override
	public String toString() {
		return toMap().toString();
	}
}
