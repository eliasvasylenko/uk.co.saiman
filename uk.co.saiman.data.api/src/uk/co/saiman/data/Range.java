package uk.co.saiman.data;

import javax.measure.Quantity;

public interface Range<U extends Quantity<U>> extends Dimension<U> {
	/**
	 * Find the interval between the smallest to the largest value of the codomain
	 * of the function within the given domain interval.
	 * 
	 * @param domainStart
	 *          The start of the domain interval whose range we wish to determine
	 * @param domainEnd
	 *          The end of the domain interval whose range we wish to determine
	 * @return The range from the smallest to the largest value of the codomain of
	 *         the function within the given interval
	 */
	Range<U> between(double domainStart, double domainEnd);
}
