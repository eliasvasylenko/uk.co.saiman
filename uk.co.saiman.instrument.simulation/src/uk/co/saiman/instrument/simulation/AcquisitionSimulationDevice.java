package uk.co.saiman.instrument.simulation;

import java.util.Random;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.strangeskies.utilities.BufferingListener;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.text.Localizer;

/**
 * Partial implementation of a simulation of an acquisition device.
 * 
 * @author Elias N Vasylenko
 */
public abstract class AcquisitionSimulationDevice extends SimulationDevice
		implements AcquisitionDevice, HardwareDevice {
	@Reference
	Localizer localizer;
	private SimulationText text;

	/**
	 * The default acquisition resolution when none is provided.
	 */
	public static final double DEFAULT_ACQUISITION_RESOLUTION = 0.00_000_025;
	/**
	 * The default acquisition frequency when none is provided.
	 */
	public static final double DEFAULT_ACQUISITION_TIME = 0.01;
	/**
	 * The default acquisition frequency when none is provided.
	 */
	public static final int DEFAULT_ACQUISITION_COUNT = 1000;

	private double acquisitionResolution;
	private double acquisitionTime;

	private int acquisitionCount;

	private SampledContinuousFunction acquisitionData;

	private final BufferingListener<SampledContinuousFunction> acquisitionListeners;
	private BufferingListener<SampledContinuousFunction> singleAcquisitionListeners;
	private BufferingListener<SampledContinuousFunction> nextAcquisitionListeners;

	private final Object acquiringLock = new Object();
	private Integer acquiringCounter;

	private boolean finalised = false;

	/**
	 * Create an acquisition simulation with the default values given by:
	 * {@link #DEFAULT_ACQUISITION_RESOLUTION} and
	 * {@link #DEFAULT_ACQUISITION_TIME}.
	 */
	public AcquisitionSimulationDevice() {
		this(DEFAULT_ACQUISITION_RESOLUTION, DEFAULT_ACQUISITION_TIME);
	}

	/**
	 * Create an acquisition simulation with the given acquisition resolution and
	 * acquisition time.
	 * 
	 * @param acquisitionResolution
	 *          The time resolution between each sample in the acquired data, in
	 *          milliseconds
	 * @param acquisitionTime
	 *          The active sampling duration for a single data acquisition, in
	 *          milliseconds.
	 */
	public AcquisitionSimulationDevice(double acquisitionResolution, double acquisitionTime) {
		nextAcquisitionListeners = new BufferingListener<>();
		acquisitionListeners = new BufferingListener<>();
		acquiringCounter = 0;

		setAcquisitionResolution(acquisitionResolution);
		setAcquisitionTime(acquisitionTime);
		setAcquisitionCount(DEFAULT_ACQUISITION_COUNT);

		new Thread(this::acquire).start();
	}

	@Activate
	void activate() {
		text = localizer.getLocalization(SimulationText.class);
	}

	protected SimulationText getText() {
		return text;
	}

	@Override
	protected void finalize() throws Throwable {
		finalised = true;
		super.finalize();
	}

	@Override
	public void addErrorListener(Consumer<Exception> exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startAcquisition() {
		synchronized (acquiringLock) {
			if (acquiringCounter > 0) {
				throw new IllegalStateException(text.alreadyAcquiring().toString());
			}

			acquiringCounter = acquisitionCount;
			singleAcquisitionListeners = nextAcquisitionListeners;
			nextAcquisitionListeners = new BufferingListener<>();
		}
	}

	private void acquire() {
		Random random = new Random();

		boolean wasAcquiring = false;
		double resolution = getAcquisitionResolution();
		int depth = getAcquisitionDepth();

		while (!finalised) {
			synchronized (acquiringLock) {
				if (!wasAcquiring) {
					resolution = getAcquisitionResolution();
					depth = getAcquisitionDepth();
				}

				wasAcquiring = acquiringCounter > 0;

				if (wasAcquiring) {
					acquiringCounter -= 1;
				}
			}

			acquisitionData = acquireImpl(random, resolution, depth);

			acquisitionListeners.accept(acquisitionData);

			if (singleAcquisitionListeners != null) {
				singleAcquisitionListeners.accept(acquisitionData);
				if (acquiringCounter == 0) {
					singleAcquisitionListeners.clearObservers();
					singleAcquisitionListeners = null;
				}
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		stopAcquisition();
	}

	protected abstract SampledContinuousFunction acquireImpl(Random random, double resolution, int depth);

	@Override
	public void stopAcquisition() {
		synchronized (acquiringLock) {
			singleAcquisitionListeners = null;

			acquiringCounter = 0;
		}
	}

	@Override
	public boolean isAcquiring() {
		return acquiringCounter > 0;
	}

	@Override
	public SampledContinuousFunction getLastAcquisitionData() {
		return acquisitionData;
	}

	@Override
	public Observable<SampledContinuousFunction> nextAcquisitionDataEvents() {
		return nextAcquisitionListeners;
	}

	@Override
	public Observable<SampledContinuousFunction> dataEvents() {
		return acquisitionListeners;
	}

	/**
	 * Set the time resolution between each sample in the acquired data.
	 * 
	 * @param resolution
	 *          The acquisition resolution in milliseconds
	 */
	public void setAcquisitionResolution(double resolution) {
		acquisitionResolution = resolution;
	}

	@Override
	public double getAcquisitionResolution() {
		return acquisitionResolution;
	}

	@Override
	public void setAcquisitionTime(double time) {
		acquisitionTime = time;
	}

	@Override
	public double getAcquisitionTime() {
		return acquisitionTime;
	}

	@Override
	public void setAcquisitionCount(int count) {
		acquisitionCount = count;
	}

	@Override
	public int getAcquisitionCount() {
		return acquisitionCount;
	}
}
