package uk.co.saiman.instrument.simulation;

import uk.co.saiman.instrument.HardwareDevice;

/**
 * Simple partial implementation of a simulation of a hardware device.
 * 
 * @author Elias N Vasylenko
 */
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
