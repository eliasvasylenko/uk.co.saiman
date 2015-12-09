package uk.co.saiman.data.api;

import uk.co.strangeskies.mathematics.Range;

public class CalibratedSampledContinuum implements SampledContinuum {
	private final SampledContinuum component;
	private final Calibration calibration;

	public CalibratedSampledContinuum(SampledContinuum component, Calibration calibration) {
		this.component = component;
		this.calibration = calibration;
	}

	@Override
	public Range<Double> getYRange() {
		return component.getYRange();
	}

	@Override
	public int getDepth() {
		return component.getDepth();
	}

	@Override
	public double getXSample(int index) {
		return calibration.calibrate(component.getXSample(index));
	}

	@Override
	public double getYSample(int index) {
		return component.getYSample(index);
	}

	@Override
	public InterpolationStrategy getInterpolationStrategy() {
		return component.getInterpolationStrategy();
	}

	@Override
	public int getIndexBelow(double xValue) {
		return component.getIndexBelow(calibration.decalibrate(xValue));
	}
}
