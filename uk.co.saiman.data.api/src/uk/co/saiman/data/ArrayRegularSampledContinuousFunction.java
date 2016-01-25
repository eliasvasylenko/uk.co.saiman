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

import uk.co.strangeskies.mathematics.expression.MutableExpressionImpl;

public class ArrayRegularSampledContinuousFunction extends MutableExpressionImpl<ContinuousFunction>
		implements RegularSampledContinuousFunction {
	private final double frequency;
	private final double[] intensities;

	public ArrayRegularSampledContinuousFunction(double frequency, double[] intensities) {
		this.frequency = frequency;

		this.intensities = intensities.clone();
	}

	@Override
	public int getDepth() {
		return intensities.length;
	}

	@Override
	public double getYSample(int index) {
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
	public ArrayRegularSampledContinuousFunction copy() {
		return new ArrayRegularSampledContinuousFunction(frequency, intensities);
	}

	@Override
	public ContinuousFunction getValueImpl(boolean dirty) {
		return this;
	}
}
