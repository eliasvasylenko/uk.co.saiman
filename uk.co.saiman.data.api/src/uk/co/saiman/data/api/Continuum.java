package uk.co.saiman.data.api;

import java.util.stream.DoubleStream;

import uk.co.strangeskies.mathematics.Range;

/**
 * TODO Difficult to genericise over data type with acceptable performance until
 * Project Valhalla, for now will just use double.
 * 
 * @author Elias N Vasylenko
 *
 * @param <V>
 */
public interface Continuum {
	Range<Double> getXRange();

	Range<Double> getYRange();

	double sampleY(double xPosition);

	default DoubleStream sampleYStream(Range<Double> between, double delta) {
		double from = between.getFrom();
		if (!between.isFromInclusive())
			from += delta;

		long count = (long) ((between.getTo() - from) / delta) + 1;
		if (!between.isToInclusive())
			count--;

		// TODO takeWhile with Java 9
		return DoubleStream.iterate(from, d -> d + delta).limit(count);
	}
}
