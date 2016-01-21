package uk.co.saiman.data;

import uk.co.strangeskies.mathematics.Range;

public interface ContinuumDecorator extends Continuum {
	Continuum getComponent();

	@Override
	default Range<Double> getXRange() {
		return getComponent().getXRange();
	}

	@Override
	default Range<Double> getYRange() {
		return getComponent().getYRange();
	}

	@Override
	default double sampleY(double xPosition) {
		return getComponent().sampleY(xPosition);
	}

	@Override
	default Range<Double> getYRange(double startX, double endX) {
		return getComponent().getYRange(startX, endX);
	}

	@Override
	default SampledContinuum resample(double startX, double endX, int resolvableUnits) {
		return getComponent().resample(startX, endX, resolvableUnits);
	}
}
