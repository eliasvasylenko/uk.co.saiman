package uk.co.saiman.data.api;

public interface RegularSampledContinuum extends SampledContinuum {
	@Override
	default double getXSample(int index) {
		return index / getFrequency();
	}

	@Override
	default int getIndexBelow(double xValue) {
		return (int) (xValue * getFrequency());
	}

	/*
	 * Samples per X unit
	 */
	public double getFrequency();
}
