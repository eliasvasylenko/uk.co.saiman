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

/**
 * An implementation of {@link SampledContinuousFunction} which maps from
 * another {@link SampledContinuousFunction} by some {@link Calibration}.
 * <p>
 * Changes in the backing function are reflected in this function.
 * 
 * @author Elias N Vasylenko
 */
public class CalibratedContinuousFunction extends DependentExpression<ContinuousFunction, ContinuousFunction>
		implements SampledContinuousFunctionDecorator {
	private final SampledContinuousFunction component;
	private final Calibration calibration;

	/**
	 * Create an instance over the given {@link SampledContinuousFunction} and
	 * {@link Calibration}.
	 * 
	 * @param component
	 *          The continuous function we wish to calibrate
	 * @param calibration
	 *          The calibration we wish to apply
	 */
	public CalibratedContinuousFunction(SampledContinuousFunction component, Calibration calibration) {
		this.component = component;
		getDependencies().add(component);

		this.calibration = calibration;
	}

	@Override
	public SampledContinuousFunction getComponent() {
		return component;
	}

	/**
	 * @return The calibration applied to the component function
	 */
	public Calibration getCalibration() {
		return calibration;
	}

	@Override
	public double getX(int index) {
		return getCalibration().calibrate(SampledContinuousFunctionDecorator.super.getX(index));
	}

	@Override
	public int getIndexBelow(double xValue) {
		return SampledContinuousFunctionDecorator.super.getIndexBelow(getCalibration().decalibrate(xValue));
	}

	@Override
	public CalibratedContinuousFunction copy() {
		return this;
	}

	@Override
	protected ContinuousFunction evaluate() {
		return this;
	}
}
