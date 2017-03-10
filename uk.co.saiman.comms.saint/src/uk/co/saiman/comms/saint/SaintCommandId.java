package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.CommandId;

public class SaintCommandId implements CommandId {
	public enum SaintCommandType {
		INPUT(0xA0), OUTPUT(0xAF);

		private final byte type;

		private SaintCommandType(int address) {
			this.type = (byte) address;
		}

		public byte getByte() {
			return type;
		}
	}

	public enum SaintCommandAddress {
		LED_LAT(0x11),
		LED_PORT(0x12),

		VACUUM_LAT(0x21),
		VACUUM_PORT(0x22),

		HV_LAT(0x31),
		HV_PORT(0x32),

		STAGE_LAT(0x41),
		STAGE_PORT(0x42),

		HV_DAC_1_LSB(0x80),
		HV_DAC_1_MSB(0x81);

		private final byte address;

		private SaintCommandAddress(int address) {
			this.address = (byte) address;
		}

		public byte getByte() {
			return address;
		}
	}

	private static final String SPLIT_CHARACTER = "::";

	private final SaintCommandType type;
	private final SaintCommandAddress address;

	public SaintCommandId(SaintCommandType type, SaintCommandAddress address) {
		this.type = type;
		this.address = address;
	}

	public SaintCommandType getType() {
		return type;
	}

	public SaintCommandAddress getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return address + SPLIT_CHARACTER + type;
	}

	@Override
	public byte[] getBytes() {
		return new byte[] { type.getByte(), address.getByte() };
	}
}
