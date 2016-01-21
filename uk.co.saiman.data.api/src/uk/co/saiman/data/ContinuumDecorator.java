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

public interface ContinuumDecorator extends Continuum {
	Continuum getComponent();

	@Override
	default Range<Double> getXRange() {
		return getComponent().getXRange();
	}

	@Override
	default Range<Double> getYRange() {
		return getComponent().getYRange();
	}

	@Override
	default double sampleY(double xPosition) {
		return getComponent().sampleY(xPosition);
	}

	@Override
	default Range<Double> getYRange(double startX, double endX) {
		return getComponent().getYRange(startX, endX);
	}

	@Override
	default SampledContinuum resample(double startX, double endX, int resolvableUnits) {
		return getComponent().resample(startX, endX, resolvableUnits);
	}
}
