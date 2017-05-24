package uk.co.saiman.comms.copley;

public enum VariableBank {
	ACTIVE, DEFAULT;

	public boolean getBit() {
		return this == DEFAULT;
	}
}
