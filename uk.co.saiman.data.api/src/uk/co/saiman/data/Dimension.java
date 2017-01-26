package uk.co.saiman.data;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.strangeskies.mathematics.Range;

/**
 * This object represents the dimension of a {@link ContinuousFunction
 * continuous function}, as well as all values of the function in that
 * dimension.
 * 
 * @author Elias N Vasylenko
 *
 * @param <U>
 *          the unit of measurement of values in this dimension
 */
public interface Dimension<U extends Quantity<U>> {
	/**
	 * Find the smallest interval containing all values in this dimension of the
	 * function it belongs to.
	 * 
	 * @return The extent of the dimension
	 */
	Range<Double> getExtent();

	/**
	 * @return the unit of measurement of values in this dimension
	 */
	Unit<U> getUnit();
}
