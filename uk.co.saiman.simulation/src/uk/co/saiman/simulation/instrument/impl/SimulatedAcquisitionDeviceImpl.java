/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.instrument.impl;

import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.annotations.Designate;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.acquisition.AcquisitionException;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.instrument.DetectorSimulation;
import uk.co.saiman.simulation.instrument.SimulatedAcquisitionDevice;
import uk.co.saiman.simulation.instrument.SimulatedDevice;
import uk.co.saiman.simulation.instrument.SimulatedSampleDevice;
import uk.co.strangeskies.text.properties.PropertyLoader;
import uk.co.strangeskies.utilities.BufferingListener;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.ObservableImpl;

/**
 * Partial implementation of a simulation of an acquisition device.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = SimulatedAcquisitionDeviceConfiguration.class)
@Component(
		configurationPid = SimulatedAcquisitionDeviceImpl.CONFIGURATION_PID,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		service = { SimulatedAcquisitionDevice.class, AcquisitionDevice.class })
public class SimulatedAcquisitionDeviceImpl implements SimulatedDevice, SimulatedAcquisitionDevice {
	static final String CONFIGURATION_PID = "uk.co.saiman.simulation.instrument.acquisition";

	private class ExperimentConfiguration {
		private final DetectorSimulation signal;
		private final SimulatedSampleDevice sample;
		private final double frequency;
		private final int depth;

		private final BufferingListener<SampledContinuousFunction<Time, Dimensionless>> listener;

		private int counter;

		private AcquisitionException exception;

		public ExperimentConfiguration() {
			frequency = getSampleFrequency();
			depth = getSampleDepth();

			listener = singleAcquisitionListeners;
			singleAcquisitionListeners = new BufferingListener<>();

			counter = getAcquisitionCount();
			if (counter <= 0) {
				throw new AcquisitionException(simulationText.acquisition().countMustBePositive());
			}

			synchronized (detectors) {
				signal = getDetector();
				if (signal == null) {
					throw new AcquisitionException(simulationText.acquisition().noSignal());
				}
				detectors.notifyAll();
			}

			sample = SimulatedAcquisitionDeviceImpl.this.sample;
		}

		public void setException(AcquisitionException exception) {
			this.exception = exception;
			counter = 0;
		}
	}

	/**
	 * The default acquisition resolution when none is provided.
	 */
	public static final double DEFAULT_ACQUISITION_RESOLUTION_SECONDS = 0.00_000_025;
	/**
	 * The default acquisition time when none is provided.
	 */
	public static final double DEFAULT_ACQUISITION_TIME_SECONDS = 0.01;
	/**
	 * The default acquisition count when none is provided.
	 */
	public static final int DEFAULT_ACQUISITION_COUNT = 1000;

	@Reference
	Units units;
	private Unit<Dimensionless> intensityUnits;
	private Unit<Time> timeUnits;

	@Reference
	PropertyLoader loader;
	private SimulationProperties simulationText;

	private final ObservableImpl<Exception> errors;

	private Quantity<Time> acquisitionResolution;
	private int acquisitionDepth;
	private int acquisitionCount;

	private SampledContinuousFunction<Time, Dimensionless> acquisitionData;
	private final BufferingListener<SampledContinuousFunction<Time, Dimensionless>> acquisitionListeners;
	private BufferingListener<SampledContinuousFunction<Time, Dimensionless>> singleAcquisitionListeners;

	private final Object acquiringLock = new Object();
	private ExperimentConfiguration experiment;

	private Set<DetectorSimulation> detectors = new HashSet<>();
	private DetectorSimulation detector;

	private Set<SimulatedSampleDevice> samples = new HashSet<>();
	private SimulatedSampleDevice sample;

	private boolean finalised = false;

	public SimulatedAcquisitionDeviceImpl() {
		errors = new ObservableImpl<>();

		singleAcquisitionListeners = new BufferingListener<>();
		acquisitionListeners = new BufferingListener<>();
	}

	@Activate
	void activate(SimulatedAcquisitionDeviceConfiguration configuration) {
		simulationText = loader.getProperties(SimulationProperties.class);

		intensityUnits = units.count().get();
		timeUnits = units.second().get();

		updated(configuration);
		setAcquisitionTime(units.second().getQuantity(DEFAULT_ACQUISITION_TIME_SECONDS));
		setAcquisitionCount(DEFAULT_ACQUISITION_COUNT);

		new Thread(this::acquire).start();
	}

	@Modified
	void updated(SimulatedAcquisitionDeviceConfiguration configuration) {
		Quantity<Time> resolution = units.parseQuantity(configuration.acquisitionResolution()).asType(Time.class);

		setAcquisitionResolution(resolution);

		if (!configuration.detectorSimulation().equals("")) {
			DetectorSimulation detector = detectors
					.stream()
					.filter(d -> d.getId().equals(configuration.detectorSimulation()))
					.findAny()
					.<AcquisitionException>orElseThrow(() -> {
						throw new AcquisitionException(
								simulationText.cannotFindDetector(configuration.detectorSimulation(), detectors));
					});

			setDetector(detector);
		}
	}

	/**
	 * @param detector
	 *          a new signal simulation option
	 */
	@Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
	public void addDetectorSimulation(DetectorSimulation detector) {
		synchronized (detectors) {
			if (detectors.add(detector) && this.detector == null) {
				this.detector = detector;
				detectors.notifyAll();
			}
		}
	}

	/**
	 * @param detector
	 *          a signal simulation option to remove
	 */
	public void removeDetectorSimulation(DetectorSimulation detector) {
		synchronized (detectors) {
			if (detectors.remove(detector) && this.detector == detector) {
				if (detectors.isEmpty()) {
					this.detector = null;
				} else {
					this.detector = detectors.iterator().next();
					detectors.notifyAll();
				}
			}
		}
	}

	@Override
	public Set<DetectorSimulation> getDetectors() {
		synchronized (detectors) {
			return unmodifiableSet(detectors);
		}
	}

	@Override
	public DetectorSimulation getDetector() {
		synchronized (detectors) {
			return detector;
		}
	}

	@Override
	public void setDetector(DetectorSimulation detector) {
		synchronized (detectors) {
			if (this.detector != detector) {
				/*-
				if (!detectors.contains(detector)) {
					throw new IllegalArgumentException();
				}
				 */
				this.detector = detector;
				detectors.notifyAll();
			}
		}
	}

	private DetectorSimulation waitForDetector() throws InterruptedException {
		synchronized (detectors) {
			DetectorSimulation detector = getDetector();

			while (detector == null) {
				detectors.wait();

				detector = getDetector();
			}

			return detector;
		}
	}

	/**
	 * @param sample
	 *          a new sample simulation option
	 */
	@Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
	public void addSampleSimulation(SimulatedSampleDevice sample) {
		synchronized (samples) {
			if (samples.add(sample) && this.sample == null) {
				this.sample = sample;
				samples.notifyAll();
			}
		}
	}

	/**
	 * @param sample
	 *          a sample simulation option to remove
	 */
	public void removeSampleSimulation(SimulatedSampleDevice sample) {
		synchronized (samples) {
			if (samples.remove(sample) && this.sample == sample) {
				if (samples.isEmpty()) {
					this.sample = null;
				} else {
					this.sample = samples.iterator().next();
					samples.notifyAll();
				}
			}
		}
	}

	@Override
	public Set<SimulatedSampleDevice> getSamples() {
		synchronized (samples) {
			return unmodifiableSet(samples);
		}
	}

	@Override
	public SimulatedSampleDevice getSample() {
		synchronized (samples) {
			return sample;
		}
	}

	@Override
	public void setSample(SimulatedSampleDevice sample) {
		synchronized (samples) {
			if (this.sample != sample) {
				/*-
				if (!samples.contains(sample)) {
					throw new IllegalArgumentException();
				}
				 */
				this.sample = sample;
				samples.notifyAll();
			}
		}
	}

	private SimulatedSampleDevice waitForSample() throws InterruptedException {
		synchronized (samples) {
			SimulatedSampleDevice sample = getSample();

			while (sample == null) {
				samples.wait();

				sample = getSample();
			}

			return sample;
		}
	}

	protected SimulationProperties getText() {
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
	public void startAcquisition(Consumer<AcquisitionDevice> nextAcquisitionDataEvents) {
		synchronized (acquiringLock) {
			nextAcquisitionDataEvents.accept(this);
			startAcquisition();
		}
	}

	@Override
	public void startAcquisition() {
		synchronized (acquiringLock) {
			if (experiment != null) {
				singleAcquisitionListeners = new BufferingListener<>();
				throw new AcquisitionException(simulationText.acquisition().alreadyAcquiring());
			}

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

	private void acquire() {
		while (!finalised) {
			SimulatedSampleDevice sample;
			DetectorSimulation detector;
			double frequency;
			int depth;

			boolean runningExperiment;
			synchronized (acquiringLock) {
				runningExperiment = experiment != null && experiment.counter-- > 0;
			}

			try {
				try {
					if (runningExperiment) {
						detector = experiment.signal;
						sample = experiment.sample;
						frequency = experiment.frequency;
						depth = experiment.depth;
					} else {
						/*
						 * This may remain blocking after an attempt to start an experiment,
						 * but this is okay as the experiment should have failed anyway if
						 * this is blocked:
						 */
						detector = waitForDetector();
						sample = waitForSample();
						frequency = getSampleFrequency();
						depth = getSampleDepth();
					}

					acquisitionData = detector.acquire(timeUnits, intensityUnits, frequency, depth, sample.getNextSample());

					acquisitionListeners.notify(acquisitionData);
					if (runningExperiment) {
						experiment.listener.notify(acquisitionData);
					}
				} catch (Exception e) {
					throw new AcquisitionException(simulationText.acquisition().unexpectedException(), e);
				}
			} catch (AcquisitionException e) {
				detector = null;
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
	public SampledContinuousFunction<Time, Dimensionless> getLastAcquisitionData() {
		return acquisitionData;
	}

	@Override
	public Observable<SampledContinuousFunction<Time, Dimensionless>> nextAcquisitionDataEvents() {
		return singleAcquisitionListeners;
	}

	@Override
	public Observable<SampledContinuousFunction<Time, Dimensionless>> dataEvents() {
		return acquisitionListeners;
	}

	/**
	 * Set the time resolution between each sample in the acquired data.
	 * 
	 * @param resolution
	 *          The acquisition resolution in milliseconds
	 */
	public void setAcquisitionResolution(Quantity<Time> resolution) {
		acquisitionResolution = resolution;
	}

	@Override
	public Quantity<Time> getSampleResolution() {
		return acquisitionResolution;
	}

	@Override
	public double getSampleFrequency() {
		return units.second().getQuantity(1).divide(getSampleResolution()).getValue().doubleValue();
	}

	@Override
	public void setAcquisitionTime(Quantity<Time> time) {
		acquisitionDepth = time.divide(getSampleResolution()).getValue().intValue();
	}

	@Override
	public Quantity<Time> getAcquisitionTime() {
		return getSampleResolution().multiply(getSampleDepth());
	}

	@Override
	public void setSampleDepth(int depth) {
		acquisitionDepth = depth;
	}

	@Override
	public int getSampleDepth() {
		return acquisitionDepth;
	}

	@Override
	public void setAcquisitionCount(int count) {
		if (count <= 0) {
			throw new AcquisitionException(simulationText.invalidAcquisitionCount(count));
		}
		acquisitionCount = count;
	}

	@Override
	public int getAcquisitionCount() {
		return acquisitionCount;
	}

	@Override
	public Unit<Time> getSampleTimeUnits() {
		return timeUnits;
	}

	@Override
	public Unit<Dimensionless> getSampleIntensityUnits() {
		return intensityUnits;
	}

	@Override
	public String toString() {
		return getName();
	}
}
