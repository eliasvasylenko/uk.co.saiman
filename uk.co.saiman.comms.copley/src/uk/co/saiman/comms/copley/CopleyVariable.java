package uk.co.saiman.comms.copley;

public enum CopleyVariable {
	LATCHED_FAULT_REGISTER(0xA1),
	TRAJECTORY_PROFILE_MODE(0xC8),
	POSITION_COMMAND(0xCA),
	AMPLIFIER_STATE(0x24),
	START_MOVE(0x01),
	START_HOMING(0x02),
	ACTUAL_POSITION(0x17);

	private final int code;

	private CopleyVariable(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static CopleyVariable forCode(byte code) {
		for (CopleyVariable variable : values())
			if (variable.getCode() == code)
				return variable;

		throw new IllegalArgumentException();
	}
}
