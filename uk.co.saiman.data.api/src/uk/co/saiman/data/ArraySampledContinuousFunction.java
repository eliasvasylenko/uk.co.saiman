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

import uk.co.strangeskies.mathematics.expression.MutableExpressionImpl;

public class ArraySampledContinuousFunction extends MutableExpressionImpl<ContinuousFunction> implements SampledContinuousFunction {
	private final double[] values;
	private final double[] intensities;

	public ArraySampledContinuousFunction(int samples, double[] values, double[] intensities) {
		this.values = Arrays.copyOf(values, samples);
		this.intensities = Arrays.copyOf(intensities, samples);
	}

	@Override
	public int getDepth() {
		return values.length;
	}

	@Override
	public int getIndexBelow(double xValue) {
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
	}

	@Override
	public double getXSample(int index) {
		return values[index];
	}

	@Override
	public double getYSample(int index) {
		return intensities[index];
	}

	@Override
	public ArraySampledContinuousFunction copy() {
		return new ArraySampledContinuousFunction(getDepth(), values, intensities);
	}

	@Override
	protected ContinuousFunction getValueImpl(boolean dirty) {
		return this;
	}
}
