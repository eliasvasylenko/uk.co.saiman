package uk.co.saiman.data.api;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import uk.co.strangeskies.mathematics.Range;

public interface SampledContinuum extends Continuum {
	@Override
	default Range<Double> getXRange() {
		return getXRange(0, getDepth() - 1);
	}

	default Range<Double> getXRange(int startIndex, int endIndex) {
		return Range.between(getXSample(0), getXSample(getDepth() - 1));
	}

	@Override
	default Range<Double> getYRange() {
		return getYRange(0, getDepth() - 1);
	}

	default Range<Double> getYRange(int startIndex, int endIndex) {
		Range<Double> yRange = Range.between(getYSample(startIndex), getYSample(startIndex));

		for (int i = startIndex; i < endIndex; i++)
			yRange = yRange.extendThrough(getYSample(i), true);

		return yRange;
	}

	default int getIndexAbove(double xValue) {
		return getIndexBelow(xValue) + 1;
	}

	int getIndexBelow(double xValue);

	int getDepth();

	double getXSample(int index);

	double getYSample(int index);

	@Override
	default double sampleY(double xPosition) {
		double yBelow = getYSample(getIndexBelow(xPosition));
		double yAbove = getYSample(getIndexAbove(xPosition));

		double xBelow = getXSample(getIndexBelow(xPosition));
		double xAbove = getXSample(getIndexAbove(xPosition));

		return getInterpolationStrategy().interpolate(yBelow, yAbove, (xAbove - xBelow) / (xPosition - xBelow));
	}

	InterpolationStrategy getInterpolationStrategy();

	default DoubleStream sampleYStream(Range<Integer> betweenIndices) {
		int from = betweenIndices.getFrom();
		if (!betweenIndices.isFromInclusive())
			from++;

		int to = betweenIndices.getTo();
		if (!betweenIndices.isToInclusive())
			to--;

		return IntStream.rangeClosed(from, to).mapToDouble(this::getYSample);
	}
}
