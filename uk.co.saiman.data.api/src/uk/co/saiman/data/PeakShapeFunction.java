package uk.co.saiman.data;

public interface PeakShapeFunction {
	double sample(double value);

	double fullWidthAtHalfMaximum();

	double effectiveDomainStart();

	double effectiveDomainEnd();
}
