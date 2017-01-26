package uk.co.saiman.data;

import javax.measure.Quantity;

/**
 * A dimension of the sample space of a sampled continuous function.
 * 
 * @author Elias N Vasylenko
 *
 * @param <U>
 *          the unit of measurement of values in this dimension
 */
public interface SampledDimension<U extends Quantity<U>> extends Dimension<U> {
	/**
	 * Find the number of samples in the continuum.
	 * 
	 * @return The depth of the sampled continuum.
	 */
	int getDepth();

	/**
	 * The value in the domain at the given index.
	 * 
	 * @param index
	 *          The sample index.
	 * @return The X value of the sample at the given index.
	 */
	double getSample(int index);
}
