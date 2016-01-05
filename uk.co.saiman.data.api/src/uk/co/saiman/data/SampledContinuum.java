/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.data.api.
 *
 * uk.co.saiman.data.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data;

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
		Range<Double> yRange = Range.between(getYSample(startIndex), getYSample(endIndex));

		for (int i = startIndex; i < endIndex; i++)
			yRange = yRange.extendThrough(getYSample(i), true);

		return yRange;
	}

	@Override
	default Range<Double> getYRange(double startX, double endX) {
		Range<Double> yRange = Range.between(startX, endX);

		yRange = yRange.extendThrough(sampleY(startX), true);
		yRange = yRange.extendThrough(sampleY(endX), true);

		for (int i = getIndexAbove(startX); i < getIndexBelow(endX); i++)
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
