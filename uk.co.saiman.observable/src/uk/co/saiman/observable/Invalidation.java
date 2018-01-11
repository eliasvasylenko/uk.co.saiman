package uk.co.saiman.observable;

import java.util.function.Function;

/**
 * A message interface for designing
 * {@link Observable#invalidateLazyRevalidate() invalidate/lazy-revalidate}
 * reactive systems. Instances represent an invalidation of the data represented
 * by an upstream {@link Observable}. The instance can be {@link #revalidate()
 * revalidated} to calculate the up-to-date state of the data.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 */
public interface Invalidation<T> {
  T revalidate();

  /**
   * Perform a mapping of the data to be revalidated. The mapping computation is
   * only applied upon revalidation.
   * 
   * @param mapping
   *          the mapping function
   * @return a new invalidation object which applies the given mapping upon
   *         revalidation
   */
  default <U> Invalidation<U> map(Function<T, U> mapping) {
    return () -> mapping.apply(revalidate());
  }
}
