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

public class CalibratedSampledContinuum implements SampledContinuum {
	private final SampledContinuum component;
	private final Calibration calibration;

	public CalibratedSampledContinuum(SampledContinuum component, Calibration calibration) {
		this.component = component;
		this.calibration = calibration;
	}

	@Override
	public Range<Double> getYRange() {
		return component.getYRange();
	}

	@Override
	public int getDepth() {
		return component.getDepth();
	}

	@Override
	public double getXSample(int index) {
		return calibration.calibrate(component.getXSample(index));
	}

	@Override
	public double getYSample(int index) {
		return component.getYSample(index);
	}

	@Override
	public InterpolationStrategy getInterpolationStrategy() {
		return component.getInterpolationStrategy();
	}

	@Override
	public int getIndexBelow(double xValue) {
		return component.getIndexBelow(calibration.decalibrate(xValue));
	}
}
