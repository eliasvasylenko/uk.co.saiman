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

	public GaussianFunctionFactory(double variance) {
		varianceAtPosition = p -> variance;
	}

	@Override
	public PeakShapeFunction atPeakPosition(double position, double intensity) {
		return new PeakShapeFunction() {
			private final double standardDeviation = Math.sqrt(varianceAtPosition.apply(position));
			private final double fullWidthHalfMaximum = standardDeviation * 2 * sqrt(2 * log(2));
			private final double peakHeightReciprocal = standardDeviation * sqrt(2 * PI);

			private final double domainStart = position - standardDeviation * 4;
			private final double domainEnd = position + standardDeviation * 4;

			@Override
			public double sample(double value) {
				double powerNumerator = value - position;
				powerNumerator *= -powerNumerator;

				double powerDenominator = standardDeviation;
				powerDenominator *= powerDenominator * 2;

				double sampleFromItem = Math.pow(Math.E, powerNumerator / powerDenominator) / peakHeightReciprocal * intensity;

				// if near edges then interpolate to 0 for smoother fall-off.
				double difference = Math.abs(value - position);
				if (difference > standardDeviation * 3) {
					sampleFromItem *= 4 - difference / standardDeviation;
				}

				return sampleFromItem;
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
