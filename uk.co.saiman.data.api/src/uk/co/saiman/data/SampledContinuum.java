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
		if (getDepth() == 0)
			return Range.between(0d, 0d).setInclusive(false, false);
		return getYRange(0, getDepth() - 1);
	}

	default Range<Double> getYRange(int startIndex, int endIndex) {
		Range<Double> yRange = Range.between(getYSample(startIndex), getYSample(endIndex));

		for (int i = startIndex; i < endIndex; i++)
			yRange.extendThrough(getYSample(i), true);

		return yRange;
	}

	@Override
	default Range<Double> getYRange(double startX, double endX) {
		if (getDepth() == 0) {
			return Range.between(0d, 0d);
		}

		double startSample = sampleY(startX);
		double endSample = sampleY(endX);

		Range<Double> yRange;
		if (getDepth() > 2) {
			yRange = getYRange(getIndexAbove(startX), getIndexBelow(endX));
		} else {
			yRange = Range.between(startSample, startSample);
		}

		yRange.extendThrough(startSample, true);
		yRange.extendThrough(endSample, true);

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
		xPosition = getXRange().getConfined(xPosition);

		int indexBelow = getIndexBelow(xPosition);
		int indexAbove = getIndexAbove(xPosition);

		if (indexBelow < 0)
			indexBelow = 0;
		if (indexAbove < 0)
			indexAbove = 0;
		if (indexBelow >= getDepth())
			indexBelow = getDepth() - 1;
		if (indexAbove >= getDepth())
			indexAbove = getDepth() - 1;

		double yBelow = getYSample(indexBelow);
		double yAbove = getYSample(indexAbove);

		double xBelow = getXSample(indexBelow);
		double xAbove = getXSample(indexAbove);

		if (xBelow == xAbove || xPosition == xBelow) {
			return yBelow;
		} else {
			return getInterpolationStrategy().interpolate(yBelow, yAbove, (xPosition - xBelow) / (xAbove - xBelow));
		}
	}

	InterpolationStrategy getInterpolationStrategy();

	@Override
	SampledContinuum copy();

	@Override
	default SampledContinuum resample(double startX, double endX, int resolvableUnits) {
		getReadLock().lock();

		try {
			if (getDepth() <= 2) {
				return copy();
			}

			int[] indices;
			double[] values;
			double[] intensities;
			int count;

			/*
			 * Prepare significant indices
			 */
			double xRange = endX - startX;
			indices = new int[resolvableUnits * 4 + 8];
			count = 0;

			int indexFrom = getIndexBelow(startX);
			if (indexFrom < 0) {
				indexFrom = 0;
			}
			int indexTo = getIndexAbove(endX);
			if (indexTo >= getDepth()) {
				indexTo = getDepth() - 1;
			}

			indices[count++] = indexFrom;

			int resolvedUnit = 0;
			double resolvableUnitLength = xRange / resolvableUnits;
			double resolvableUnitFrequency = resolvableUnits / xRange;
			double resolvedUnitX = startX;

			int lastIndex;
			int minIndex;
			double minY;
			int maxIndex;
			double maxY;
			lastIndex = minIndex = maxIndex = indexFrom;
			minY = maxY = getYSample(lastIndex);
			for (int index = indexFrom + 1; index < indexTo; index++) {
				/*
				 * Get sample location at index
				 */
				double sampleX = getXSample(index);
				double sampleY = getYSample(index);

				/*
				 * Check if passed resolution boundary (or last position)
				 */
				if (sampleX > resolvedUnitX || index + 1 == indexTo) {
					/*
					 * Move to next resolution boundary
					 */
					resolvedUnit = (int) ((sampleX - startX) * resolvableUnitFrequency) + 1;
					resolvedUnitX = startX + resolvedUnit * resolvableUnitLength;

					/*
					 * Add indices of minimum and maximum y encountered in boundary span
					 */
					if (sampleY < minY) {
						minIndex = -1;
					} else if (sampleY > maxY) {
						maxIndex = -1;
					}

					if (minIndex > 0) {
						if (maxIndex > 0) {
							if (maxIndex > minIndex) {
								indices[count++] = minIndex;
								indices[count++] = maxIndex;
							} else {
								indices[count++] = maxIndex;
								indices[count++] = minIndex;
							}
						} else {
							indices[count++] = minIndex;
						}
					} else if (maxIndex > 0) {
						indices[count++] = maxIndex;
					}

					if (index > lastIndex) {
						indices[count++] = index;
					}
					lastIndex = index + 1;
					indices[count++] = lastIndex;

					minIndex = -1;
					maxIndex = -1;
				} else if (index > lastIndex) {
					/*
					 * Check for Y range expansion
					 */
					if (maxIndex == -1 || sampleY > maxY) {
						maxY = sampleY;
						maxIndex = index;
					} else if (minIndex == -1 || sampleY < minY) {
						minY = sampleY;
						minIndex = index;
					}
				}
			}

			/*
			 * Prepare significant values
			 */
			values = new double[count];
			for (int i = 0; i < count; i++) {
				values[i] = getXSample(indices[i]);
			}

			/*
			 * Prepare significant intensities
			 */
			intensities = new double[count];
			for (int i = 0; i < count; i++) {
				intensities[i] = getYSample(indices[i]);
			}

			/*
			 * Prepare linearisation
			 */
			return new SimpleSampledContinuum(count, values, intensities);
		} finally {
			getReadLock().unlock();
		}
	}
}
