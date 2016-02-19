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
 * A partial implementation of a {@link SampledContinuousFunction} decorator, to
 * reduce boilerplate for functions which are in some way mappings of other
 * functions.
 * 
 * @author Elias N Vasylenko
 */
public interface SampledContinuousFunctionDecorator extends SampledContinuousFunction, ContinuousFunctionDecorator {
	@Override
	public SampledContinuousFunction getComponent();

	@Override
	default Range<Double> getRange() {
		return getComponent().getRange();
	}

	@Override
	default int getDepth() {
		return getComponent().getDepth();
	}

	@Override
	default double getX(int index) {
		return getComponent().getX(index);
	}

	@Override
	default double getY(int index) {
		return getComponent().getY(index);
	}

	@Override
	default int getIndexBelow(double xValue) {
		return getComponent().getIndexBelow(xValue);
	}

	@Override
	default double sample(double xPosition) {
		return ContinuousFunctionDecorator.super.sample(xPosition);
	}

	@Override
	default Range<Double> getRangeBetween(double startX, double endX) {
		return ContinuousFunctionDecorator.super.getRangeBetween(startX, endX);
	}

	@Override
	default Range<Double> getDomain() {
		return ContinuousFunctionDecorator.super.getDomain();
	}

	@Override
	default SampledContinuousFunction resample(double startX, double endX, int resolvableUnits) {
		return ContinuousFunctionDecorator.super.resample(startX, endX, resolvableUnits);
	}
}
