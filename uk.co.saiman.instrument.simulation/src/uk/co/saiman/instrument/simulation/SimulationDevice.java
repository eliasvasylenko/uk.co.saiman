package uk.co.saiman.instrument.simulation;

import uk.co.saiman.instrument.HardwareDevice;

public abstract class SimulationDevice implements HardwareDevice {
	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void reset() {
		abortOperation();
	}
}
