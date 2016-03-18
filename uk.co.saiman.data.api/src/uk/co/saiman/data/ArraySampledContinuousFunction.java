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

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * A mutable, array backed implementation of {@link SampledContinuousFunction}.
 * 
 * @author Elias N Vasylenko
 */
public class ArraySampledContinuousFunction extends LockingSampledContinuousFunction
		implements SampledContinuousFunction {
	private final double[] values;
	private final double[] intensities;

	/**
	 * Instantiate with the given number of samples, values, and intensities.
	 * Arrays are copied into the function, truncated to the sample length given,
	 * or padded with 0s.
	 * 
	 * @param samples
	 *          The number of samples in the function
	 * @param values
	 *          The X values of the samples, in the domain
	 * @param intensities
	 *          The Y values of the samples, in the codomain
	 */
	public ArraySampledContinuousFunction(int samples, double[] values, double[] intensities) {
		/*
		 * TODO sort values
		 */
		this.values = Arrays.copyOf(values, samples);
		this.intensities = Arrays.copyOf(intensities, samples);
	}

	/**
	 * Safely modify the data of this sampled continuous function.
	 * 
	 * @param valueMutation
	 *          The mutation operation on the values of the samples, i.e. the X
	 *          values, in the domain
	 * @param intensityMutation
	 *          The mutation operation on the intensities of the samples, i.e. the
	 *          Y values, in the codomain
	 */
	public void mutateIntensities(Consumer<double[]> valueMutation, Consumer<double[]> intensityMutation) {
		getWriteLock().lock();
		try {
			valueMutation.accept(values);
			intensityMutation.accept(intensities);
		} finally {
			getWriteLock().unlock();
		}
	}

	@Override
	public int getDepth() {
		return read(() -> values.length);
	}

	@Override
	public int getIndexBelow(double xValue) {
		getReadLock().lock();

		try {
			int from = 0;
			int to = values.length - 1;

			if (to < 0) {
				return -1;
			}

			do {
				if (values[to] <= xValue) {
					return to;
				} else if (values[from] > xValue) {
					return -1;
				} else if (values[from] == xValue) {
					return from;
				} else {
					int mid = (to + from) / 2;
					if (values[mid] > xValue) {
						to = mid;
					} else {
						from = mid;
					}
				}
			} while (to - from > 1);

			return from;
		} finally {
			getReadLock().unlock();
		}
	}

	@Override
	public double getX(int index) {
		return read(() -> values[index]);
	}

	@Override
	public double getY(int index) {
		return read(() -> intensities[index]);
	}

	@Override
	public ArraySampledContinuousFunction copy() {
		return read(() -> new ArraySampledContinuousFunction(getDepth(), values, intensities));
	}

	@Override
	protected ContinuousFunction evaluate() {
		return this;
	}
}
