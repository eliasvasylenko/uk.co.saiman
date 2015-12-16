package uk.co.saiman.msapex.data;

import uk.co.saiman.data.Continuum;

public interface ChartDataAnnotation<T> extends ChartAnnotation<T> {
	@Override
	double getX();

	Continuum getContinuum();

	@Override
	default double getY() {
		return getContinuum().sampleY(getX());
	}
}
