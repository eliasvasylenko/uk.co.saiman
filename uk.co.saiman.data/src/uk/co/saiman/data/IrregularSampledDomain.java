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

/**
 * A partial-implementation of {@link SampledContinuousFunction} with regular
 * intervals in the domain between samples.
 * 
 * @param <U>
 *          the type of the units of measurement of values in the domain
 * @author Elias N Vasylenko
 */
public class IrregularSampledDomain<U extends Quantity<U>> implements SampledDomain<U> {
	private final Unit<U> unit;

	private final double[] values;

	public IrregularSampledDomain(Unit<U> unit, double[] values) {
		this.unit = unit;
		this.values = values.clone();
	}

	@Override
	public double getSample(int index) {
		return values[index];
	}

	@Override
	public int getIndexBelow(double xValue) {
		int from = 0;
		int to = values.length - 1;

		if (to < 0) {
			return -1;
		}

		do {
			if (values[to] <= xValue) {
				return to;
			} else if (values[from] > xValue) {
				return -1;
			} else if (values[from] == xValue) {
				return from;
			} else {
				int mid = (to + from) / 2;
				if (values[mid] > xValue) {
					to = mid;
				} else {
					from = mid;
				}
			}
		} while (to - from > 1);

		return from;
	}

	@Override
	public Unit<U> getUnit() {
		return unit;
	}

	@Override
	public int getDepth() {
		return values.length;
	}
}
