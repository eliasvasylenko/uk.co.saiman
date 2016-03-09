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

/**
 * A partial-implementation of {@link ContinuousFunction} for sampled continua.
 * The model is as a sequence of (X, Y) points, with (X) increasing in the
 * domain with each index, starting at 0.
 * 
 * @author Elias N Vasylenko
 */
public interface SampledContinuousFunction extends ContinuousFunction {
	@Override
	default Range<Double> getDomain() {
		return getDomain(0, getDepth() - 1);
	}

	/**
	 * Find the interval in the domain described by the given sample indices.
	 * 
	 * @param startIndex
	 *          The index of the sample at the beginning of the interval
	 * @param endIndex
	 *          The index of the sample at the end of the interval
	 * @return The extent of the samples between those given
	 */
	default Range<Double> getDomain(int startIndex, int endIndex) {
		return Range.between(getX(0), getX(getDepth() - 1));
	}

	@Override
	default Range<Double> getRange() {
		if (getDepth() == 0)
			return Range.between(0d, 0d).setInclusive(false, false);
		return getRangeBetween(0, getDepth() - 1);
	}

	/**
	 * Find the interval between the smallest to the largest value of the codomain
	 * of the function within the interval in the domain described by the given
	 * sample indices.
	 * 
	 * @param startIndex
	 *          The index of the sample at the beginning of the domain interval
	 *          whose range we wish to determine
	 * @param endIndex
	 *          The index of the sample at the end of the domain interval whose
	 *          range we wish to determine
	 * @return The range from the smallest to the largest value of the codomain of
	 *         the function within the given interval
	 */
	default Range<Double> getRangeBetween(int startIndex, int endIndex) {
		if (startIndex < 0)
			startIndex = 0;
		if (endIndex >= getDepth())
			endIndex = getDepth() - 1;

		Range<Double> yRange = Range.between(getY(startIndex), getY(endIndex));

		for (int i = startIndex; i < endIndex; i++)
			yRange.extendThrough(getY(i), true);

		return yRange;
	}

	@Override
	default Range<Double> getRangeBetween(double startX, double endX) {
		if (getDepth() == 0) {
			return Range.between(0d, 0d);
		}

		double startSample = sample(startX);
		double endSample = sample(endX);

		Range<Double> yRange;
		if (getDepth() > 2) {
			yRange = getRangeBetween(getIndexAbove(startX), getIndexBelow(endX));
		} else {
			yRange = Range.between(startSample, startSample);
		}

		yRange.extendThrough(startSample, true);
		yRange.extendThrough(endSample, true);

		return yRange;
	}

	/**
	 * Find the nearest index with a value on the domain above the value given.
	 * 
	 * @param xValue
	 *          The value we wish to find the nearest greater sampled neighbour
	 *          to.
	 * @return The index of the sample adjacent and above the given value, or -1
	 *         if no such sample exists.
	 */
	default int getIndexAbove(double xValue) {
		int index = getIndexBelow(xValue) + 1;
		if (index >= getDepth()) {
			index = -1;
		}
		return index;
	}

	/**
	 * Find the nearest index with a value on the domain below, or equal to, the
	 * value given.
	 * 
	 * @param xValue
	 *          The value we wish to find the nearest lower sampled neighbour to.
	 * @return The index of the sample adjacent and below the given value, or -1
	 *         if no such sample exists.
	 */
	int getIndexBelow(double xValue);

	/**
	 * Find the number of samples in the continuum.
	 * 
	 * @return The depth of the sampled continuum.
	 */
	int getDepth();

	/**
	 * The value in the domain at the given index.
	 * 
	 * @param index
	 *          The sample index.
	 * @return The X value of the sample at the given index.
	 */
	double getX(int index);

	/**
	 * The value in the codomain at the given index.
	 * 
	 * @param index
	 *          The sample index.
	 * @return The Y value of the sample at the given index.
	 */
	double getY(int index);

	@Override
	default double sample(double xPosition) {
		xPosition = getDomain().getConfined(xPosition);

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

		double yBelow = getY(indexBelow);
		double yAbove = getY(indexAbove);

		double xBelow = getX(indexBelow);
		double xAbove = getX(indexAbove);

		if (xBelow == xAbove || xPosition == xBelow) {
			return yBelow;
		} else {
			return yBelow + (yAbove - yBelow) * (xPosition - xBelow) / (xAbove - xBelow);
		}
	}

	@Override
	SampledContinuousFunction copy();

	@Override
	default SampledContinuousFunction resample(double startX, double endX, int resolvableUnits) {
		synchronized (this) {
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
			if (indexTo >= getDepth() || indexTo < 0) {
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
			minY = maxY = getY(lastIndex);
			for (int index = indexFrom + 1; index < indexTo; index++) {
				/*
				 * Get sample location at index
				 */
				double sampleX = getX(index);
				double sampleY = getY(index);

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
				values[i] = getX(indices[i]);
			}

			/*
			 * Prepare significant intensities
			 */
			intensities = new double[count];
			for (int i = 0; i < count; i++) {
				intensities[i] = getY(indices[i]);
			}

			/*
			 * Prepare linearisation
			 */
			return new ArraySampledContinuousFunction(count, values, intensities);
		}
	}
}
