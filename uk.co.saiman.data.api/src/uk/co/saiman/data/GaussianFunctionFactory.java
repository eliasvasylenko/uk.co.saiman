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

import static java.lang.Math.PI;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

import java.util.function.Function;

/**
 * Gaussian implementation of {@link PeakShapeFunctionFactory}.
 * 
 * @author Elias N Vasylenko
 */
public class GaussianFunctionFactory implements PeakShapeFunctionFactory {
	private final Function<Double, Double> varianceAtPosition;

	/**
	 * @param variance
	 *          the uniform variance for peaks at any point across the entire
	 *          domain
	 */
	public GaussianFunctionFactory(double variance) {
		varianceAtPosition = p -> variance;
	}

	@Override
	public PeakShapeFunction atPeakPosition(double mean, double intensity) {
		return new PeakShapeFunction() {
			private final double standardDeviation = Math.sqrt(varianceAtPosition.apply(mean));
			private final double fullWidthHalfMaximum = standardDeviation * 2 * sqrt(2 * log(2));
			private final double peakHeightReciprocal = standardDeviation * sqrt(2 * PI);

			private final double domainStart = mean - standardDeviation * 4;
			private final double domainEnd = mean + standardDeviation * 4;

			@Override
			public double sample(double value) {
				double powerNumerator = value - mean;
				powerNumerator *= -powerNumerator;

				double powerDenominator = standardDeviation;
				powerDenominator *= powerDenominator * 2;

				double sampleFromItem = Math.pow(Math.E, powerNumerator / powerDenominator) / peakHeightReciprocal * intensity;

				// if near edges then interpolate to 0 for smoother fall-off.
				double difference = Math.abs(value - mean);
				if (difference > standardDeviation * 3) {
					sampleFromItem *= 4 - difference / standardDeviation;
				}

				return sampleFromItem;
			}

			@Override
			public double maximum() {
				return mean;
			}

			@Override
			public double mean() {
				return mean;
			}

			@Override
			public double fullWidthAtHalfMaximum() {
				return fullWidthHalfMaximum;
			}

			@Override
			public double effectiveDomainStart() {
				return domainStart;
			}

			@Override
			public double effectiveDomainEnd() {
				return domainEnd;
			}
		};
	}
}
