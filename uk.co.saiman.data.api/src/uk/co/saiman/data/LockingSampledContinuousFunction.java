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
import uk.co.strangeskies.mathematics.expression.LockingExpression;

/**
 * A simple abstract partial implementation of a
 * {@link SampledContinuousFunction} which ensures all default method
 * implementations lock for reading.
 * 
 * @author Elias N Vasylenko
 */
public abstract class LockingSampledContinuousFunction extends LockingExpression<ContinuousFunction, ContinuousFunction>
		implements SampledContinuousFunction {
	@Override
	public Range<Double> getDomain() {
		return read(SampledContinuousFunction.super::getDomain);
	}

	@Override
	public Range<Double> getDomain(int startIndex, int endIndex) {
		return read(() -> SampledContinuousFunction.super.getDomain(startIndex, endIndex));
	}

	@Override
	public double sample(double xPosition) {
		return read(() -> SampledContinuousFunction.super.sample(xPosition));
	}

	@Override
	public int getIndexAbove(double xValue) {
		return read(() -> SampledContinuousFunction.super.getIndexAbove(xValue));
	}

	@Override
	public Range<Double> getRange() {
		return read(SampledContinuousFunction.super::getRange);
	}

	@Override
	public Range<Double> getRangeBetween(double startX, double endX) {
		return read(() -> SampledContinuousFunction.super.getRangeBetween(startX, endX));
	}

	@Override
	public Range<Double> getRangeBetween(int startIndex, int endIndex) {
		return read(() -> SampledContinuousFunction.super.getRangeBetween(startIndex, endIndex));
	}

	@Override
	public SampledContinuousFunction resample(double startX, double endX, int resolvableUnits) {
		return read(() -> SampledContinuousFunction.super.resample(startX, endX, resolvableUnits));
	}
}
