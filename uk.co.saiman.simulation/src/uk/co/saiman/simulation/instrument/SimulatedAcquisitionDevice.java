package uk.co.saiman.simulation.instrument;

import java.util.Set;

import uk.co.saiman.acquisition.AcquisitionDevice;

public interface SimulatedAcquisitionDevice extends AcquisitionDevice, SimulatedDevice {
	/**
	 * @return the signal detector simulations available for use
	 */
	public Set<DetectorSimulation> getDetectors();

	/**
	 * @return the signal detector simulation currently in use
	 */
	public DetectorSimulation getDetector();

	/**
	 * @param detector
	 *          the new signal detector simulation to use
	 */
	public void setDetector(DetectorSimulation detector);

	/**
	 * @return the sample device simulations available for use
	 */
	public Set<SimulatedSampleDevice> getSamples();

	/**
	 * @return the sample device simulation currently in use
	 */
	public SimulatedSampleDevice getSample();

	/**
	 * @param sample
	 *          the new sample device simulation to use
	 */
	public void setSample(SimulatedSampleDevice sample);
}
