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

import uk.co.strangeskies.mathematics.expression.ImmutableExpression;

class EmptyContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends ImmutableExpression<ContinuousFunction<UD, UR>> implements SampledContinuousFunction<UD, UR> {
	private final SampledDomain<UD> domain;
	private final SampledRange<UR> range;

	protected EmptyContinuousFunction(Unit<UD> unitDomain, Unit<UR> unitRange) {
		this.domain = new EmptyDomain<>(unitDomain);
		this.range = new EmptyRange<>(this, unitRange);
	}

	@Override
	public SampledDomain<UD> domain() {
		return domain;
	}

	@Override
	public SampledRange<UR> range() {
		return range;
	}

	@Override
	public SampledContinuousFunction<UD, UR> copy() {
		return this;
	}

	@Override
	public ContinuousFunction<UD, UR> getValue() {
		return this;
	}

	@Override
	public SampledContinuousFunction<UD, UR> resample(SampledDomain<UD> resolvableSampleDomain) {
		return this;
	}

	@Override
	public int getDepth() {
		return 1;
	}
}

class EmptyDomain<UD extends Quantity<UD>> implements SampledDomain<UD> {
	private final Unit<UD> unitDomain;

	public EmptyDomain(Unit<UD> unitDomain) {
		this.unitDomain = unitDomain;
	}

	@Override
	public Unit<UD> getUnit() {
		return unitDomain;
	}

	@Override
	public int getDepth() {
		return 1;
	}

	@Override
	public double getSample(int index) {
		if (index != 0)
			throw new ArrayIndexOutOfBoundsException(index);
		return 0;
	}

	@Override
	public int getIndexBelow(double xValue) {
		if (xValue >= 0)
			return 0;
		else
			return -1;
	}
}

class EmptyRange<UR extends Quantity<UR>> extends SampledRange<UR> {
	private final Unit<UR> unitRange;

	public EmptyRange(EmptyContinuousFunction<?, UR> function, Unit<UR> unitRange) {
		super(function);
		this.unitRange = unitRange;
	}

	@Override
	public Unit<UR> getUnit() {
		return unitRange;
	}

	@Override
	public int getDepth() {
		return 1;
	}

	@Override
	public double getSample(int index) {
		if (index != 0)
			throw new ArrayIndexOutOfBoundsException(index);
		return 0;
	}
}
