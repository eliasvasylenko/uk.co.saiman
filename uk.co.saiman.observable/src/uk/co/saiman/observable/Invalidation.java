/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
