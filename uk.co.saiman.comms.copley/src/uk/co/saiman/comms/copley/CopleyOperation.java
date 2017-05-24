package uk.co.saiman.comms.copley;

public enum CopleyOperation {
	NO_OP(0x00),
	GET_OPERATING_MODE(0x07),
	GET_FLASH_CRC(0x0A),
	SWITCH_OPERATING_MODE(0x11),

	GET_VARIABLE(0x0C),
	GET_DEFAULT(0x0C),

	SET_VARIABLE(0x0D),
	SET_DEFAULT(0x0D),

	COPY_VARIABLE(0x0E),
	SAVE_VARIABLE(0x0E),
	LOAD_VARIABLE(0x0E),

	TRACE_VARIABLE(0x0F),
	RESET(0x10),
	TRAJECTORY(0x11),
	ERROR_LOG(0x12),
	COPLEY_VIRTUAL_MACHINE(0x14),
	ENCODER(0x1B),
	GET_CAN_OBJECT(0x1C),
	SET_CAN_OBJECT(0x1D),
	DYNAMIC_FILE_INTERFACE(0x21);

	private final byte code;

	private CopleyOperation(int code) {
		this.code = (byte) code;
	}

	public byte getCode() {
		return code;
	}

	public static CopleyOperation getCanonicalOperation(byte code) {
		for (CopleyOperation operation : CopleyOperation.values()) {
			if (operation.getCode() == code)
				return operation;
		}

		throw new IllegalArgumentException("Unknown code " + code);
	}
}
