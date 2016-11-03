/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
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
import uk.co.strangeskies.mathematics.expression.Expression;
import uk.co.strangeskies.mathematics.expression.SelfExpression;
import uk.co.strangeskies.utilities.Observer;

/**
 * A set of domain values (X) mapped via some continuous function to a
 * corresponding value in the range (Y). The domain is defined by a single
 * inclusive interval, and the function is defined for all values in the domain.
 * <p>
 * {@link ContinuousFunction}s may be mutable, depending on implementation, but
 * in this case they should notify listeners by way of the API provided through
 * {@link SelfExpression}.
 * <p>
 * TODO: Difficult to genericise over data type with acceptable performance
 * until Project Valhalla, for now will just use double.
 * 
 * @author Elias N Vasylenko
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 */
public interface ContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends SelfExpression<ContinuousFunction<UD, UR>>, Expression<ContinuousFunction<UD, UR>> {
	/**
	 * @param unitDomain
	 *          the units of the domain
	 * @param unitRange
	 *          the units of the range
	 * @return a simple, immutable instance of a continuous function describing a
	 *         single point at 0 in both the domain and codomain according to the
	 *         given units
	 */
	static <UD extends Quantity<UD>, UR extends Quantity<UR>> ContinuousFunction<UD, UR> empty(Unit<UD> unitDomain,
			Unit<UR> unitRange) {
		return new EmptyContinuousFunction<>(unitDomain, unitRange);
	}

	/**
	 * Find the smallest interval containing all values in the domain of the
	 * function.
	 * 
	 * @return The extent of the domain
	 */
	Range<Double> getDomain();

	/**
	 * @return the units of measurement of values in the domain
	 */
	Unit<UD> getDomainUnit();

	/**
	 * An interval containing all values in the codomain of the function. Some
	 * implementations may not quickly be able to calculate the smallest such
	 * interval, in which case they should return a close approximation
	 * guaranteed, to contain the exact smallest interval.
	 * 
	 * @return The extent of the codomain
	 */
	Range<Double> getRange();

	/**
	 * @return the units of measurement of values in the range
	 */
	Unit<UR> getRangeUnit();

	/**
	 * Find the interval between the smallest to the largest value of the codomain
	 * of the function within the given domain interval.
	 * 
	 * @param startX
	 *          The start of the domain interval whose range we wish to determine
	 * @param endX
	 *          The end of the domain interval whose range we wish to determine
	 * @return The range from the smallest to the largest value of the codomain of
	 *         the function within the given interval
	 */
	Range<Double> getRangeBetween(double startX, double endX);

	/**
	 * Find the resulting Y value of the function for a given X.
	 * 
	 * @param xPosition
	 *          The X value to resolve
	 * @return The Y value for the given X
	 */
	double sample(double xPosition);

	/**
	 * Sometimes it is useful to linearize a continuous function in order to
	 * perform complex processing, or for display purposes. This method provides a
	 * linear view of any continuous function. Implementations may remove features
	 * which are unresolvable at the given resolution.
	 * <p>
	 * Typically a good implementation should attempt to roughly indicate the
	 * minimum and maximum within a resolvable unit area, such that thin peaks and
	 * troughs don't disappear from visual representation at low resolution.
	 * 
	 * @param startX
	 *          The start of the interval we wish to sample over
	 * @param endX
	 *          The end of the interval we wish to sample over
	 * @param resolvableUnits
	 *          The minimum resolvable units within the given interval
	 * @return A new sampled, linear continuous function roughly equal to the
	 *         receiver to the requested resolution
	 */
	SampledContinuousFunction<UD, UR> resample(double startX, double endX, int resolvableUnits);
}

class EmptyContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		implements SampledContinuousFunction<UD, UR> {
	private final Unit<UD> unitDomain;
	private final Unit<UR> unitRange;

	public EmptyContinuousFunction(Unit<UD> unitDomain, Unit<UR> unitRange) {
		this.unitDomain = unitDomain;
		this.unitRange = unitRange;
	}

	@Override
	public SampledContinuousFunction<UD, UR> copy() {
		return this;
	}

	@Override
	public boolean removeObserver(Observer<? super Expression<? extends ContinuousFunction<UD, UR>>> observer) {
		return true;
	}

	@Override
	public boolean addObserver(Observer<? super Expression<? extends ContinuousFunction<UD, UR>>> observer) {
		return true;
	}

	@Override
	public ContinuousFunction<UD, UR> getValue() {
		return this;
	}

	@Override
	public SampledContinuousFunction<UD, UR> resample(double startX, double endX, int resolvableUnits) {
		return this;
	}

	@Override
	public int getIndexBelow(double xValue) {
		if (xValue >= 0)
			return 0;
		else
			return -1;
	}

	@Override
	public int getDepth() {
		return 1;
	}

	@Override
	public double getX(int index) {
		if (index != 0)
			throw new ArrayIndexOutOfBoundsException(index);
		return 0;
	}

	@Override
	public double getY(int index) {
		return getX(index);
	}

	@Override
	public Unit<UD> getDomainUnit() {
		return unitDomain;
	}

	@Override
	public Unit<UR> getRangeUnit() {
		return unitRange;
	}
};
