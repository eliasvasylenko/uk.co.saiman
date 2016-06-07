package uk.co.saiman.instrument.simulation.impl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.acquisition.AcquisitionException;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.saiman.instrument.simulation.SampleSimulation;
import uk.co.saiman.instrument.simulation.SignalSimulation;
import uk.co.saiman.instrument.simulation.SimulationDevice;
import uk.co.saiman.instrument.simulation.SimulationText;
import uk.co.strangeskies.utilities.BufferingListener;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.ObservableImpl;
import uk.co.strangeskies.utilities.text.Localizer;

/**
 * Partial implementation of a simulation of an acquisition device.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = { AcquisitionDevice.class, HardwareDevice.class, AcquisitionSimulationDevice.class })
public class AcquisitionSimulationDevice extends SimulationDevice implements AcquisitionDevice {
	private class ExperimentConfiguration {
		private final SignalSimulation signal;
		private final SampleSimulation sample;
		private final double resolution;
		private final int depth;

		private final BufferingListener<SampledContinuousFunction> listener;

		private int counter;

		private AcquisitionException exception;

		public ExperimentConfiguration() {
			resolution = getAcquisitionResolution();
			depth = getAcquisitionDepth();

			listener = singleAcquisitionListeners;
			singleAcquisitionListeners = new BufferingListener<>();

			counter = getAcquisitionCount();
			if (counter <= 0) {
				throw new AcquisitionException(simulationText.acquisition().countMustBePositive());
			}

			synchronized (signals) {
				signal = getSignal();
				if (signal == null) {
					throw new AcquisitionException(simulationText.acquisition().noSignal());
				}
				signals.notifyAll();
			}

			sample = AcquisitionSimulationDevice.this.sample;
		}

		public void setException(AcquisitionException exception) {
			this.exception = exception;
			counter = 0;
		}
	}

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

	@Reference
	Localizer localizer;
	private SimulationText simulationText;

	private final ObservableImpl<Exception> errors;

	private double acquisitionResolution;
	private int acquisitionDepth;

	private int acquisitionCount;

	private SampledContinuousFunction acquisitionData;
	private final BufferingListener<SampledContinuousFunction> acquisitionListeners;
	private BufferingListener<SampledContinuousFunction> singleAcquisitionListeners;

	private final Object acquiringLock = new Object();
	private ExperimentConfiguration experiment;

	private boolean finalised = false;
	private Set<SignalSimulation> signals = new HashSet<>();
	private SignalSimulation signal;
	@Reference
	private SampleSimulation sample;

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
		errors = new ObservableImpl<>();

		singleAcquisitionListeners = new BufferingListener<>();
		acquisitionListeners = new BufferingListener<>();

		setAcquisitionResolution(acquisitionResolution);
		setAcquisitionTime(acquisitionTime);
		setAcquisitionCount(DEFAULT_ACQUISITION_COUNT);
	}

	@Activate
	void activate() {
		simulationText = localizer.getLocalization(SimulationText.class);

		new Thread(this::acquire).start();
	}

	/**
	 * @param signal
	 *          a new signal simulation option
	 */
	@Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
	public void addSignalSimulation(SignalSimulation signal) {
		synchronized (signals) {
			if (signals.add(signal) && this.signal == null) {
				this.signal = signal;
				signals.notifyAll();
			}
		}
	}

	/**
	 * @param signal
	 *          a signal simulation option to remove
	 */
	public void removeSignalSimulation(SignalSimulation signal) {
		synchronized (signals) {
			if (signals.remove(signal) && this.signal == signal) {
				if (signals.isEmpty()) {
					this.signal = null;
				} else {
					this.signal = signals.iterator().next();
					signals.notifyAll();
				}
			}
		}
	}

	/**
	 * @return the signal simulation currently in use
	 */
	public SignalSimulation getSignal() {
		synchronized (signals) {
			return signal;
		}
	}

	/**
	 * @param signal
	 *          the new signal simulation to use
	 */
	public void setSignal(SignalSimulation signal) {
		synchronized (signals) {
			if (this.signal != signal) {
				if (!signals.contains(signal)) {
					throw new IllegalArgumentException();
				}
				this.signal = signal;
				signals.notifyAll();
			}
		}
	}

	protected SimulationText getText() {
		return simulationText;
	}

	@Override
	protected void finalize() throws Throwable {
		finalised = true;
		super.finalize();
	}

	@Override
	public String getName() {
		return simulationText.acquisitionSimulationDeviceName().toString();
	}

	@Override
	public Observable<Exception> errors() {
		return errors;
	}

	@Override
	public void startAcquisition() {
		synchronized (acquiringLock) {
			if (experiment != null) {
				throw new IllegalStateException(simulationText.acquisition().alreadyAcquiring().toString());
			}

			if (acquisitionCount > 0) {
				ExperimentConfiguration experiment = this.experiment = new ExperimentConfiguration();

				while (experiment == this.experiment) {
					try {
						acquiringLock.wait();
					} catch (InterruptedException e) {
						experiment.counter = 0;
						throw new AcquisitionException(simulationText.acquisition().experimentInterrupted(), e);
					}
				}

				if (experiment.exception != null) {
					throw experiment.exception;
				}
			}
		}
	}

	private SignalSimulation waitForSignal() throws InterruptedException {
		synchronized (signals) {
			SignalSimulation signal = getSignal();

			while (signal == null) {
				signals.wait();

				signal = getSignal();
			}

			return signal;
		}
	}

	private SampleSimulation getSample() {
		return this.sample;
	}

	private void acquire() {
		Random random = new Random();

		while (!finalised) {
			SampleSimulation sample;
			SignalSimulation signal;
			double resolution;
			int depth;

			boolean runningExperiment;
			synchronized (acquiringLock) {
				runningExperiment = experiment != null && experiment.counter-- > 0;
			}

			try {
				try {
					if (runningExperiment) {
						signal = experiment.signal;
						sample = experiment.sample;
						resolution = experiment.resolution;
						depth = experiment.depth;
					} else {
						/*
						 * This may remain blocking after an attempt to start an experiment,
						 * but this is okay as the experiment should have failed anyway if
						 * this is blocked:
						 */
						signal = waitForSignal();
						sample = getSample();
						resolution = getAcquisitionResolution();
						depth = getAcquisitionDepth();
					}

					acquisitionData = signal.acquire(random, resolution, depth, sample.getNextSample());

					acquisitionListeners.accept(acquisitionData);
					if (runningExperiment) {
						experiment.listener.accept(acquisitionData);
					}
				} catch (Exception e) {
					throw new AcquisitionException(simulationText.acquisition().unexpectedException(), e);
				}
			} catch (AcquisitionException e) {
				signal = null;
				if (runningExperiment) {
					experiment.setException(e);
				} else {
					/*
					 * TODO logging from this class...
					 */
					e.printStackTrace();
				}
			}

			synchronized (acquiringLock) {
				if (runningExperiment && experiment.counter <= 0) {
					experiment = null;
					acquiringLock.notifyAll();
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

	@Override
	public void stopAcquisition() {
		synchronized (acquiringLock) {
			if (experiment != null) {
				experiment.counter = 0;
			}
		}
	}

	@Override
	public boolean isAcquiring() {
		return experiment != null;
	}

	@Override
	public SampledContinuousFunction getLastAcquisitionData() {
		return acquisitionData;
	}

	@Override
	public Observable<SampledContinuousFunction> nextAcquisitionDataEvents() {
		return singleAcquisitionListeners;
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
		acquisitionDepth = (int) (time / getAcquisitionResolution());
	}

	@Override
	public double getAcquisitionTime() {
		return getAcquisitionResolution() * getAcquisitionDepth();
	}

	@Override
	public void setAcquisitionDepth(int depth) {
		acquisitionDepth = depth;
	}

	@Override
	public int getAcquisitionDepth() {
		return acquisitionDepth;
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
