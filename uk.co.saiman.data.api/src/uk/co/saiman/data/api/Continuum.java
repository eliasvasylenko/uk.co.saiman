package uk.co.saiman.data.api;

import java.util.stream.Stream;

import uk.co.strangeskies.mathematics.Range;

public interface Continuum<X extends Number & Comparable<? super X>, Y extends Number & Comparable<? super Y>> {
	Range<X> getXRange();

	Range<Y> getYRange();

	Y sampleY(X xPosition);

	Stream<Y> sampleYStream(Range<X> between, X delta);
}
