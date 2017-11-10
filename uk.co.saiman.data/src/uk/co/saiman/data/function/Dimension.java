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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.function;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.mathematics.Interval;

/**
 * This object represents the dimension of a {@link ContinuousFunction
 * continuous function}, as well as all values of the function in that
 * dimension.
 * 
 * @author Elias N Vasylenko
 *
 * @param <U>
 *          the unit of measurement of values in this dimension
 */
public interface Dimension<U extends Quantity<U>> {
	/**
	 * Find the smallest interval containing all values in this dimension of the
	 * function it belongs to.
	 * 
	 * @return The extent of the dimension
	 */
	Interval<Double> getInterval();

	/**
	 * @return the unit of measurement of values in this dimension
	 */
	Unit<U> getUnit();
}
