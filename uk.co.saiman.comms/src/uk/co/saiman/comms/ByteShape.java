package uk.co.saiman.comms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ByteShape {
	private final List<String> bits;

	public ByteShape(
			String bit0,
			String bit1,
			String bit2,
			String bit3,
			String bit4,
			String bit5,
			String bit6,
			String bit7) {
		bits = new ArrayList<>(8);
		bits.add(bit0);
		bits.add(bit1);
		bits.add(bit2);
		bits.add(bit3);
		bits.add(bit4);
		bits.add(bit5);
		bits.add(bit6);
		bits.add(bit7);
	}

	public Stream<String> getBits() {
		return bits.stream();
	}
}
