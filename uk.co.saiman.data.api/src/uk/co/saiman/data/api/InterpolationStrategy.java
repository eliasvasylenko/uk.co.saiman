package uk.co.saiman.data.api;

public interface InterpolationStrategy {
	double interpolate(double from, double to, double delta);
}
