package uk.co.saiman.comms.saint;

import java.util.BitSet;

public class StatusLedBits {
	private final BitSet bits;

	public StatusLedBits() {
		bits = new BitSet(Byte.SIZE);
	}

	public StatusLedBits(byte data) {
		bits = BitSet.valueOf(new byte[] { data });
	}

	public boolean isOn(int index) {
		return bits.get(index);
	}

	private StatusLedBits setOn(int index, boolean on) {
		bits.set(index, on);
		return this;
	}

	public StatusLedBits withOn(int index, boolean on) {
		return new StatusLedBits(getByte()).setOn(index, on);
	}

	public StatusLedBits withOn(int index) {
		return withOn(index, true);
	}

	public StatusLedBits withOff(int index) {
		return withOn(index, false);
	}

	public byte getByte() {
		return bits.toByteArray()[0];
	}
}
