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

import uk.co.strangeskies.mathematics.expression.DependentExpression;

public class CalibratedSampledContinuum extends DependentExpression<Continuum> implements SampledContinuumDecorator {
	private final SampledContinuum component;
	private final Calibration calibration;

	public CalibratedSampledContinuum(SampledContinuum component, Calibration calibration) {
		this.component = component;
		getDependencies().add(component);

		this.calibration = calibration;
	}

	@Override
	public SampledContinuum getComponent() {
		return component;
	}

	public Calibration getCalibration() {
		return calibration;
	}

	@Override
	public double getXSample(int index) {
		return getCalibration().calibrate(SampledContinuumDecorator.super.getXSample(index));
	}

	@Override
	public int getIndexBelow(double xValue) {
		return SampledContinuumDecorator.super.getIndexBelow(getCalibration().decalibrate(xValue));
	}

	@Override
	public CalibratedSampledContinuum copy() {
		return this;
	}

	@Override
	protected Continuum evaluate() {
		return this;
	}
}
