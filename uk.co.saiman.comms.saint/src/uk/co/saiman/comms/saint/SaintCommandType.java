package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.CommsException;

public enum SaintCommandType {
	PING(0x00), INPUT(0xA0), OUTPUT(0xAF);

	private final byte type;

	private SaintCommandType(int address) {
		this.type = (byte) address;
	}

	public byte getByte() {
		return type;
	}

	public static SaintCommandType fromByte(byte data) {
		for (SaintCommandType type : values()) {
			if (type.getByte() == data) {
				return type;
			}
		}
		throw new CommsException("Command type not found " + data);
	}
}
