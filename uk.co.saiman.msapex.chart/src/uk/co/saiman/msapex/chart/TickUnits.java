package uk.co.saiman.msapex.chart;

import javax.measure.Quantity;

/**
 * A strategy of automatically adjusting the {@link Range} of a
 * {@link QuantityAxis} based on its data contents. The range object specifies
 * the interval which is displayed, as well as the tick units and label
 * formatting used.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 */
public interface TickUnits<T extends Quantity<T>> {
  TickUnit<T> getUnitBelow(double valueAbove);
}
