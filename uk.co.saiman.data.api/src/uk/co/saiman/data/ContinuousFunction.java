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
import uk.co.strangeskies.mathematics.expression.SelfExpression;

/**
 * A set of domain values (X) mapped via some continuous function to a
 * corresponding value in the range (Y). The domain is defined by a single
 * inclusive interval, and the function is defined for all values in the domain.
 * <p>
 * TODO: Difficult to genericise over data type with acceptable performance
 * until Project Valhalla, for now will just use double.
 * 
 * @author Elias N Vasylenko
 */
public interface ContinuousFunction extends SelfExpression<ContinuousFunction> {
	/**
	 * The smallest interval containing all values in the domain of the function.
	 * 
	 * @return The extent of the domain
	 */
	Range<Double> getDomain();

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
	 * @param startX
	 *          The start of the interval whose range we wish to determine
	 * @param endX
	 *          The end of the interval whose range we wish to determine
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
	 * Sometimes it is useful to linearise a continuous function in order to
	 * perform complex processing, or for display purposes. This method provides a
	 * linear view of any continuous function. Implementations may remove features
	 * which are unresolvable at the given resolution.
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
	SampledContinuousFunction resample(double startX, double endX, int resolvableUnits);
}
