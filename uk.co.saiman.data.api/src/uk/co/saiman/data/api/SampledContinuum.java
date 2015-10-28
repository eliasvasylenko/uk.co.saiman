package uk.co.saiman.data.api;

import uk.co.strangeskies.mathematics.Range;

public interface SampledContinuum<X extends Number & Comparable<? super X>, Y extends Number & Comparable<? super Y>>
		extends Continuum<X, Y> {
	Range<X> getXRange(int startIndex, int endIndex);

	Range<Y> getYRange(int startIndex, int endIndex);

	Range<Integer> getIndicesBetween(Range<X> xRange);

	int getLength();

	X getXSample(int index);

	Y getYSample(int index);

	void getInterpolationStrategy();
}
