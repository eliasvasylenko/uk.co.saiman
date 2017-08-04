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
 * A partial implementation of a {@link ContinuousFunction} decorator, to reduce
 * boilerplate for functions which are in some way mappings of other functions.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public interface ContinuousFunctionDecorator<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends ContinuousFunction<UD, UR> {
	/**
	 * @return The {@link ContinuousFunction} backing this decorator
	 */
	ContinuousFunction<UD, UR> getComponent();

	@Override
	default Domain<UD> domain() {
		return getComponent().domain();
	}

	@Override
	default Range<UR> range() {
		return getComponent().range();
	}

	@Override
	default double sample(double xPosition) {
		return getComponent().sample(xPosition);
	}

	@Override
	default SampledContinuousFunction<UD, UR> resample(SampledDomain<UD> resolvableSampleDomain) {
		return getComponent().resample(resolvableSampleDomain);
	}
}
