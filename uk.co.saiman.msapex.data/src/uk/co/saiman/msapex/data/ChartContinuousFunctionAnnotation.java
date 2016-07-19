/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.data.
 *
 * uk.co.saiman.msapex.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.data;

import uk.co.saiman.data.ContinuousFunction;

/**
 * A typed data annotation on a chart at a specific location on a continuous
 * function in that chart.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The type of the data of the annotation
 */
public interface ChartContinuousFunctionAnnotation<T> extends ChartAnnotation<T> {
	@Override
	double getX();

	/**
	 * @return The continuous function this annotation should apply to.
	 */
	ContinuousFunction getContinuousFunction();

	/**
	 * @return The position in the domain of the continuous function in the chart
	 */
	@Override
	default double getY() {
		return getContinuousFunction().sample(getX());
	}
}
