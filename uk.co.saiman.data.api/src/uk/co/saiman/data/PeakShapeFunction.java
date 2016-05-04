package uk.co.saiman.data;

/**
 * An interface for describing a peak shape function which can be sampled over
 * an effective area. The peak shape may be generated from samples, or it may be
 * defined mathematically as e.g. a Gaussian or Lorentz distribution.
 * 
 * <p>
 * A peak is assumed to have negligible intensity at the
 * {@link #effectiveDomainStart() start} and {@link #effectiveDomainEnd() end}
 * of its effective domain, and only a single {@link #maximum() local maximum}.
 * 
 * @author Elias N Vasylenko
 */
public interface PeakShapeFunction {
	/**
	 * @param value
	 *          the input value in the domain to sample the result in the codomain
	 * @return the output of the function at the given input
	 */
	double sample(double value);

	/**
	 * @return the point in the domain at which the function is at its maximum
	 *         result
	 */
	double maximum();

	/**
	 * @return the mean centre of the peak
	 */
	double mean();

	/**
	 * @return the width between
	 */
	double fullWidthAtHalfMaximum();

	/**
	 * @return the lowest useful value beyond which intensity is negligible
	 */
	double effectiveDomainStart();

	/**
	 * @return the highest useful value beyond which intensity is negligible
	 */
	double effectiveDomainEnd();
}
