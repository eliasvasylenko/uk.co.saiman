/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.Arrays;

import uk.co.strangeskies.mathematics.Range;

public class StickedContinuum implements Continuum {
	private final Range<Double> xRange;
	private final double[] xPositions;
	private final double[] intensities;

	public StickedContinuum(Range<Double> xRange, int sticks, double[] xPositions, double[] intensities) {
		this.xRange = xRange;

		this.xPositions = Arrays.copyOf(xPositions, sticks);
		this.intensities = Arrays.copyOf(intensities, sticks);
	}

	@Override
	public Range<Double> getXRange() {
		return xRange;
	}

	@Override
	public Range<Double> getYRange() {
		Range<Double> yRange = Range.between(0d, 0d);

		for (double intensity : intensities)
			yRange.extendThrough(intensity, true);

		return yRange;
	}

	@Override
	public Range<Double> getYRange(double startX, double endX) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double sampleY(double xPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
}
