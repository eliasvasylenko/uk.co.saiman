package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.StatusBits;

public class HighVoltageBits extends StatusBits<HighVoltageBit> {
	public HighVoltageBits() {
		super(HighVoltageBit.class);
	}

	public HighVoltageBits(byte bytes) {
		super(HighVoltageBit.class, new byte[] { bytes });
	}

	@Override
	public StatusBits<HighVoltageBit> withSet(HighVoltageBit bit, boolean set) {
		return new HighVoltageBits(getByte()).set(bit, set);
	}

	public byte getByte() {
		return super.getBytes()[0];
	}
}
