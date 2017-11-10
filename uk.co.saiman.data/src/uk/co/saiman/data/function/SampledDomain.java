/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.function;

import javax.measure.Quantity;

import uk.co.saiman.mathematics.Interval;

/**
 * Implementations of this class must be immutable, though it is not guaranteed
 * than an implementation of {@link SampledContinuousFunction} will return the
 * same instance for each invocation of {@link ContinuousFunction#domain()}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <U>
 *          the unit of measurement of values in this dimension
 */
public interface SampledDomain<U extends Quantity<U>> extends Domain<U>, SampledDimension<U> {
  @Override
  default Interval<Double> getInterval() {
    if (getDepth() == 0) {
      return getExtent(0, 0);
    } else {
      return getExtent(0, getDepth() - 1);
    }
  }

  /**
   * Find the interval in the domain described by the given sample indices.
   * 
   * @param startIndex
   *          The index of the sample at the beginning of the interval
   * @param endIndex
   *          The index of the sample at the end of the interval
   * @return The extent of the samples between those given
   */
  default Interval<Double> getExtent(int startIndex, int endIndex) {
    return Interval.bounded(getSample(startIndex), getSample(endIndex));
  }

  /**
   * Find the nearest index with a value on the domain above the value given.
   * 
   * @param xValue
   *          The value we wish to find the nearest greater sampled neighbour
   *          to.
   * @return The index of the sample adjacent and above the given value, or -1
   *         if no such sample exists.
   */
  default int getIndexAbove(double xValue) {
    int index = getIndexBelow(xValue) + 1;
    if (index >= getDepth()) {
      index = -1;
    }
    return index;
  }

  /**
   * Find the nearest index with a value on the domain below, or equal to, the
   * value given.
   * 
   * @param xValue
   *          The value we wish to find the nearest lower sampled neighbour to.
   * @return The index of the sample adjacent and below the given value, or -1
   *         if no such sample exists.
   */
  int getIndexBelow(double xValue);
}
