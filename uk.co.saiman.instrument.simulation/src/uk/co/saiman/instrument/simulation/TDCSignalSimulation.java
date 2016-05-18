/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.acquisition.
 *
 * uk.co.saiman.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.simulation;

import java.util.Random;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.SampledContinuousFunction;
import uk.co.saiman.data.SparseSampledContinuousFunction;
import uk.co.saiman.instrument.HardwareDevice;
import uk.co.strangeskies.utilities.BufferingListener;
import uk.co.strangeskies.utilities.Observable;
import uk.co.strangeskies.utilities.text.LocalizedString;
import uk.co.strangeskies.utilities.text.LocalizedText;
import uk.co.strangeskies.utilities.text.Localizer;

/**
 * A configurable software simulation of an acquisition hardware module.
 * 
 * @author Elias N Vasylenko
 */
@Component(service = { AcquisitionDevice.class, HardwareDevice.class })
public class TDCSignalSimulation implements AcquisitionDevice {
	public interface AcquisitionText extends LocalizedText<AcquisitionText> {
		LocalizedString simulationTDCDeviceName();

		LocalizedString alreadyAcquiring();
	}

	@Reference
	Localizer localizer;
	AcquisitionText text;

	/**
	 * The default acquisition resolution when none is provided.
	 */
	public static final double DEFAULT_ACQUISITION_RESOLUTION = 0.00_000_001;
	/**
	 * The default acquisition frequency when none is provided.
	 */
	public static final double DEFAULT_ACQUISITION_TIME = 0.000_01;
	/**
	 * The default acquisition frequency when none is provided.
	 */
	public static final int DEFAULT_ACQUISITION_COUNT = 1000;

	private double acquisitionResolution = DEFAULT_ACQUISITION_RESOLUTION;
	private double acquisitionTime = DEFAULT_ACQUISITION_TIME;

	private int acquisitionCount = DEFAULT_ACQUISITION_COUNT;

	private SampledContinuousFunction acquisitionData;
	private final BufferingListener<SampledContinuousFunction> singleAcquisitionListeners;
	private final BufferingListener<SampledContinuousFunction> acquisitionListeners;

	private final Object acquiringLock = new Object();
	private Integer acquiringCounter;

	private static final int MAXIMUM_HITS = 10;
	private final int[] hitIndices;
	private final double[] hitIntensities;

	private boolean finalised = false;

	/**
	 * Create an acquisition simulation with the default values given by:
	 * {@link #DEFAULT_ACQUISITION_RESOLUTION} and
	 * {@link #DEFAULT_ACQUISITION_TIME}.
	 */
	public TDCSignalSimulation() {
		singleAcquisitionListeners = new BufferingListener<>();
		acquisitionListeners = new BufferingListener<>();
		acquiringCounter = 0;

		hitIndices = new int[MAXIMUM_HITS];
		hitIntensities = new double[MAXIMUM_HITS];
		for (int i = 0; i < MAXIMUM_HITS; i++) {
			hitIntensities[i] = 1;
		}

		new Thread(this::acquire).start();
	}

	@Activate
	void activate() {
		text = localizer.getLocalization(AcquisitionText.class);
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
	public TDCSignalSimulation(double acquisitionResolution, double acquisitionTime) {
		this();
		setAcquisitionResolution(acquisitionResolution);
		setAcquisitionTime(acquisitionTime);
	}

	@Override
	protected void finalize() throws Throwable {
		finalised = true;
		super.finalize();
	}

	@Override
	public String getName() {
		return text.simulationTDCDeviceName().toString();
	}

	@Override
	public boolean isConnected() {
		return true;
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
		}
	}

	private void acquire() {
		Random random = new Random();

		while (!finalised) {
			boolean acquired;

			synchronized (acquiringLock) {
				acquired = acquiringCounter > 0;
				if (acquired) {
					acquiringCounter -= 1;
				}
			}

			int hits = random.nextInt(MAXIMUM_HITS);

			/*
			 * TODO distribute "hits" number of hits
			 */

			acquisitionData = new SparseSampledContinuousFunction(1 / getAcquisitionResolution(), getAcquisitionDepth(), hits,
					hitIndices, hitIntensities);

			acquisitionListeners.accept(acquisitionData);

			if (acquired) {
				singleAcquisitionListeners.accept(acquisitionData);
				if (acquiringCounter == 0) {
					singleAcquisitionListeners.clearObservers();
				}
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		stopAcquisition();
	}

	@Override
	public void stopAcquisition() {
		synchronized (acquiringLock) {
			singleAcquisitionListeners.clearObservers();

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
