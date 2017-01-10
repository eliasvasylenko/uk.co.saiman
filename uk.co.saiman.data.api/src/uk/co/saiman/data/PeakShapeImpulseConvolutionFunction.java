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

import static uk.co.strangeskies.mathematics.Range.between;

import java.util.Arrays;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.strangeskies.mathematics.Range;
import uk.co.strangeskies.mathematics.expression.ImmutableExpression;

/**
 * A function described by a convolution operation over a given set of impulses,
 * or stick intensities, by a given peak shape.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public class PeakShapeImpulseConvolutionFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
		extends ImmutableExpression<ContinuousFunction<UD, UR>> implements ContinuousFunction<UD, UR> {
	private static final int TWEEN_STEPS = 5;

	private final Unit<UD> unitDomain;
	private final Unit<UR> unitRange;

	private final int samples;
	private final double[] values;
	private final PeakShapeFunction[] peakFunctions;

	private final Range<Double> domain;
	private final Range<Double> range;

	/**
	 * Define a new function by way of convolution of the given samples by the
	 * given peak shape description.
	 * 
	 * @param unitDomain
	 *          the units of measurement of values in the domain
	 * @param unitRange
	 *          the units of measurement of values in the range
	 * @param samples
	 *          the number of contributing samples
	 * @param values
	 *          the sorted sample positions
	 * @param intensities
	 *          the intensities corresponding to the given values
	 * @param peakFunctionFactory
	 *          the peak function by which to convolve
	 */
	public PeakShapeImpulseConvolutionFunction(Unit<UD> unitDomain, Unit<UR> unitRange, int samples, double[] values,
			double[] intensities, PeakShapeFunctionFactory peakFunctionFactory) {
		this.unitDomain = unitDomain;
		this.unitRange = unitRange;

		/*
		 * TODO sort values
		 */
		this.samples = samples;
		this.values = Arrays.copyOf(values, samples);

		peakFunctions = new PeakShapeFunction[samples];
		for (int i = 0; i < samples; i++) {
			peakFunctions[i] = peakFunctionFactory.atPeakPosition(values[i], intensities[i]);
		}

		domain = between(peakFunctions[0].effectiveDomainStart(), peakFunctions[samples - 1].effectiveDomainEnd());

		range = between(0d, getRangeBetween(values[0], values[samples - 1]).getTo());
	}

	@Override
	public Unit<UD> getDomainUnit() {
		return unitDomain;
	}

	@Override
	public Unit<UR> getRangeUnit() {
		return unitRange;
	}

	@Override
	public ContinuousFunction<UD, UR> getValue() {
		return this;
	}

	@Override
	public ContinuousFunction<UD, UR> copy() {
		return this;
	}

	@Override
	public Range<Double> getDomain() {
		return domain;
	}

	@Override
	public Range<Double> getRange() {
		return range;
	}

	@Override
	public Range<Double> getRangeBetween(double startX, double endX) {
		return getRangeBetween(startX, endX, 0, samples - 1);
	}

	/*
	 * Estimate range in codomain by sampling at the centre of each stick
	 * position, and at various points between each stick position.
	 */
	protected Range<Double> getRangeBetween(double startX, double endX, int startIndex, int endIndex) {
		double previousValue = values[startIndex];

		double startSample = sample(startX);
		double endSample = sample(endX);

		double maximum;
		double minimum;
		if (startSample > endSample) {
			maximum = startSample;
			minimum = endSample;
		} else {
			maximum = endSample;
			minimum = startSample;
		}

		// Sample first index
		if (previousValue >= startX && previousValue <= endX) {
			double intensity = sample(previousValue);
			if (intensity > maximum)
				maximum = intensity;
			else if (intensity < minimum)
				minimum = intensity;
		}

		for (int i = startIndex + 1; i <= endIndex; i++) {
			double value = values[i];

			if (value >= startX) {
				double subValueStep = (value - previousValue) / TWEEN_STEPS;
				double subValue = previousValue;

				for (int j = 0; j < TWEEN_STEPS; j++) {
					subValue += subValueStep;

					if (subValue >= startX && subValue <= (endX + subValueStep)) {
						double intensity = sample(subValue);
						if (intensity > maximum)
							maximum = intensity;
						else if (intensity < minimum)
							minimum = intensity;
					}
				}

				if (value >= endX)
					break;
			}

			previousValue = value;
		}

		return Range.between(minimum, maximum);
	}

	@Override
	public double sample(double xPosition) {
		double sample = 0;
		for (int i = 0; i < samples; i++) {
			if (peakFunctions[i].effectiveDomainEnd() > xPosition)
				sample += peakFunctions[i].sample(xPosition);

			if (peakFunctions[i].effectiveDomainStart() > xPosition)
				break;
		}
		return sample;
	}

	@Override
	public SampledContinuousFunction<UD, UR> resample(double startX, double endX, int resolvableUnits) {
		int maximumLength = resolvableUnits * 3 + 1;
		double[] values = new double[maximumLength];
		double[] intensities = new double[maximumLength];

		int sampleCount = 0;
		double stepSize = (endX - startX) / resolvableUnits;
		double samplePosition = startX;
		double nextSamplePosition = startX;

		for (int i = 0; i < resolvableUnits; i++) {
			nextSamplePosition += stepSize;

			values[sampleCount] = samplePosition;
			intensities[sampleCount] = sample(samplePosition);
			sampleCount++;

			// TODO maxima & minima in interval

			samplePosition = nextSamplePosition;
		}

		values[sampleCount] = endX;
		intensities[sampleCount] = sample(endX);

		return new ArraySampledContinuousFunction<>(getDomainUnit(), getRangeUnit(), sampleCount, values, intensities);
	}
}
