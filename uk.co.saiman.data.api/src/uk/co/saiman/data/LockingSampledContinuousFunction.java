/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
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

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.strangeskies.mathematics.Range;
import uk.co.strangeskies.mathematics.expression.LockingExpression;

/**
 * A simple abstract partial implementation of a
 * {@link SampledContinuousFunction} which ensures all default method
 * implementations lock for reading.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public abstract class LockingSampledContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends LockingExpression<ContinuousFunction<UD, UR>> implements SampledContinuousFunction<UD, UR> {
	private final Unit<UD> unitDomain;
	private final Unit<UR> unitRange;

	/**
	 * @param unitDomain
	 *          the units of measurement of values in the domain
	 * @param unitRange
	 *          the units of measurement of values in the range
	 */
	public LockingSampledContinuousFunction(Unit<UD> unitDomain, Unit<UR> unitRange) {
		this.unitDomain = unitDomain;
		this.unitRange = unitRange;
	}

	@Override
	public Unit<UD> getDomainUnit() {
		return unitDomain;
	}

	@Override
	public Unit<UR> getRangeUnit() {
		return unitRange;
	}

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
	public SampledContinuousFunction<UD, UR> resample(double startX, double endX, int resolvableUnits) {
		return read(() -> SampledContinuousFunction.super.resample(startX, endX, resolvableUnits));
	}

	@Override
	public ContinuousFunction<UD, UR> decoupleValue() {
		return getValue().copy();
	}
}
