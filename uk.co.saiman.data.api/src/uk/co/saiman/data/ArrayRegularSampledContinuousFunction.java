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

import java.util.function.Consumer;

import uk.co.strangeskies.mathematics.expression.LockingExpressionImpl;

/**
 * A mutable implementation conforming to the contract of
 * {@link RegularSampledContinuousFunction} and backed by an array.
 * 
 * @author Elias N Vasylenko
 */
public class ArrayRegularSampledContinuousFunction extends LockingExpressionImpl<ContinuousFunction, ContinuousFunction>
		implements RegularSampledContinuousFunction {
	private final double frequency;
	private final double domainStart;
	private final double[] intensities;

	/**
	 * Create an instance with the given frequency, intensities, and domain value
	 * of first index.
	 * 
	 * @param frequency
	 *          The frequency as per {@link #getFrequency()}
	 * @param domainStart
	 *          The domain value of the sample at the first index
	 * @param intensities
	 *          The intensities of each sample in sequence
	 */
	public ArrayRegularSampledContinuousFunction(double frequency, double domainStart, double[] intensities) {
		this.frequency = frequency;
		this.domainStart = domainStart;

		this.intensities = intensities.clone();
	}

	@Override
	public int getDepth() {
		return intensities.length;
	}

	@Override
	public double getY(int index) {
		return intensities[index];
	}

	/**
	 * Safely modify the data of this sampled continuous function.
	 * 
	 * @param mutation
	 *          The mutation operation
	 */
	public void mutate(Consumer<double[]> mutation) {
		getReadLock().lock();
		try {
			mutation.accept(intensities);
		} finally {
			getReadLock().unlock();
		}
	}

	@Override
	public double getFrequency() {
		return frequency;
	}

	@Override
	public double getX(int index) {
		return RegularSampledContinuousFunction.super.getX(index) + domainStart;
	}

	@Override
	public ArrayRegularSampledContinuousFunction copy() {
		return new ArrayRegularSampledContinuousFunction(frequency, domainStart, intensities);
	}

	@Override
	public ContinuousFunction getValueImpl(boolean dirty) {
		return this;
	}
}
