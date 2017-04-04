package uk.co.saiman.comms;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class NumberedBits {
	public static final String PREFIX_SEPARATOR = "_";

	private final String prefix;
	private final BitSet bits;
	private final int size;

	public NumberedBits(String prefix) {
		this(prefix, Byte.SIZE);
	}

	public NumberedBits(String prefix, int size) {
		this.prefix = prefix;
		this.bits = new BitSet(size);
		this.size = size;
	}

	public NumberedBits(String prefix, int size, byte[] bytes) {
		this.prefix = prefix;
		this.bits = BitSet.valueOf(bytes);
		this.size = size;
	}

	public boolean isSet(int index) {
		checkBounds(index);
		return bits.get(index);
	}

	private NumberedBits set(int index, boolean on) {
		checkBounds(index);
		bits.set(index, on);
		return this;
	}

	private void checkBounds(int index) {
		if (index >= size)
			throw new ArrayIndexOutOfBoundsException();
	}

	public NumberedBits withSet(int index, boolean on) {
		return new NumberedBits(prefix, size, getBytes()).set(index, on);
	}

	public NumberedBits withSet(int index) {
		return withSet(index, true);
	}

	public NumberedBits withUnset(int index) {
		return withSet(index, false);
	}

	public byte[] getBytes() {
		return bits.toByteArray();
	}

	public int getCount() {
		return size;
	}

	public Map<String, Boolean> toMap() {
		Map<String, Boolean> map = new LinkedHashMap<>();

		for (int i = 0; i < size; i++) {
			map.put(prefix + PREFIX_SEPARATOR + i, isSet(i));
		}

		return map;
	}

	@Override
	public String toString() {
		return toMap().toString();
	}
}
