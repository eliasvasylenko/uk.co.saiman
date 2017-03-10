package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.StatusBits;

public class VacuumBits extends StatusBits<VacuumBit> {
	public VacuumBits() {
		super(VacuumBit.class);
	}

	public VacuumBits(byte bytes) {
		super(VacuumBit.class, new byte[] { bytes });
	}

	@Override
	public StatusBits<VacuumBit> withSet(VacuumBit bit, boolean set) {
		return new VacuumBits(getByte()).set(bit, set);
	}

	public byte getByte() {
		return super.getBytes()[0];
	}
}
