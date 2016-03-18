package uk.co.saiman.data;

/**
 * Factory for generating normalised {@link PeakShapeFunction}s at a given
 * position, and of a given total intensity.
 * <p>
 * Peaks may differ in shape according to position, e.g. to model degrading
 * effective mass resolution at higher masses, though the change should be
 * continuous with change in position.
 * <p>
 * Peaks at higher positions should always return higher values for
 * {@link PeakShapeFunction#effectiveDomainStart()} and
 * {@link PeakShapeFunction#effectiveDomainEnd()}.
 * 
 * @author Elias N Vasylenko
 */
public interface PeakShapeFunctionFactory {
	/**
	 * @param position
	 *          the position of the peak in the domain axis
	 * @param intensity
	 *          the total intensity of the peak, i.e. the integral over the peak
	 * @return a peak shape function for the given position and intensity
	 */
	PeakShapeFunction atPeakPosition(double position, double intensity);
}
