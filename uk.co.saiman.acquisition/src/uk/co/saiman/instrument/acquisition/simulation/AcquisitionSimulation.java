package uk.co.saiman.instrument.acquisition.simulation;

import java.util.Random;
import java.util.function.Consumer;

import uk.co.saiman.data.api.SampledContinuum;
import uk.co.saiman.data.api.SparseSampledContinuum;
import uk.co.saiman.instrument.acquisition.AcquisitionModule;
import uk.co.saiman.utilities.BufferingListener;

public class AcquisitionSimulation implements AcquisitionModule {
	private double acquisitionResolution = 0.00_000_1;
	private double acquisitionFrequency = 10_000;
	private double acquisitionTime;

	private SampledContinuum acquisitionData;
	private final BufferingListener<SampledContinuum> singleAcquisitionListeners;
	private final BufferingListener<SampledContinuum> acquisitionListeners;

	private Boolean isAcquiring;

	private static final int MAXIMUM_HITS = 10;
	private final int[] hitIndices;
	private final double[] hitIntensities;

	public AcquisitionSimulation() {
		singleAcquisitionListeners = new BufferingListener<>();
		acquisitionListeners = new BufferingListener<>();
		isAcquiring = false;

		hitIndices = new int[MAXIMUM_HITS];
		hitIntensities = new double[MAXIMUM_HITS];
		for (int i = 0; i < MAXIMUM_HITS; i++) {
			hitIntensities[i] = 1;
		}
	}

	@Override
	public String getName() {
		return "Acquisition Simulation";
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
		synchronized (isAcquiring) {
			if (isAcquiring) {
				throw new IllegalStateException("Cannot start new acquisition, already acquiring");
			}

			for (Consumer<? super SampledContinuum> listener : singleAcquisitionListeners.getListeners()) {
				if (acquisitionListeners.getListeners().contains(listener)) {
					singleAcquisitionListeners.removeListener(listener);
				} else {
					acquisitionListeners.addListener(listener);
				}
			}

			isAcquiring = true;

			new Thread(this::acquire).start();
		}
	}

	public void acquire() {
		Random random = new Random();

		for (int i = 0; i < acquisitionTime * acquisitionFrequency; i++) {
			int hits = random.nextInt(MAXIMUM_HITS);

			/*
			 * TODO distribute "hits" number of hits
			 */

			acquisitionData = new SparseSampledContinuum(1 / getAcquisitionResolution(), getAcquisitionDepth(), hits,
					hitIndices, hitIntensities);

			acquisitionListeners.accept(acquisitionData);

			/*
			 * TODO wait
			 */
		}
		stopAcquisition();
	}

	@Override
	public void stopAcquisition() {
		synchronized (isAcquiring) {
			for (Consumer<? super SampledContinuum> listener : singleAcquisitionListeners.getListeners()) {
				acquisitionListeners.removeListener(listener);
			}
			singleAcquisitionListeners.removeAllListeners();

			isAcquiring = false;
		}
	}

	@Override
	public boolean isAcquiring() {
		return isAcquiring;
	}

	@Override
	public SampledContinuum getLastAcquisitionData() {
		return acquisitionData;
	}

	@Override
	public void addSingleAcquisitionListener(Consumer<? super SampledContinuum> listener) {
		singleAcquisitionListeners.addListener(listener);
	}

	@Override
	public void addAcquisitionListener(Consumer<? super SampledContinuum> listener) {
		acquisitionListeners.addListener(listener);
	}

	@Override
	public void removeAcquisitionListener(Consumer<? super SampledContinuum> listener) {
		acquisitionListeners.removeListener(listener);
	}

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
}
