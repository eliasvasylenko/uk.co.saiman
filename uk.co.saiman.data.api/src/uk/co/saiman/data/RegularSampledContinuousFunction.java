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

/**
 * A partial-implementation of {@link SampledContinuousFunction} with regular
 * intervals in the domain between samples.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public interface RegularSampledContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends SampledContinuousFunction<UD, UR> {
	@Override
	default double getX(int index) {
		if (index < 0 || index > getDepth())
			throw new ArrayIndexOutOfBoundsException(index);
		return index / getFrequency();
	}

	@Override
	default int getIndexBelow(double xValue) {
		return (int) (xValue * getFrequency());
	}

	/**
	 * @return The number of samples per unit in the domain
	 */
	public double getFrequency();
}
