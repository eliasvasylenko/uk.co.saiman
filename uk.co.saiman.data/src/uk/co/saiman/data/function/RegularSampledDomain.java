/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.Objects.requireNonNull;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * A partial-implementation of {@link SampledContinuousFunction} with regular
 * intervals in the domain between samples.
 * 
 * @param <U>
 *          the type of the units of measurement of values in the domain
 * @author Elias N Vasylenko
 */
public class RegularSampledDomain<U extends Quantity<U>> implements SampledDomain<U> {
  private final Unit<U> unit;

  private final int depth;
  private final double frequency;
  private final double start;

  public RegularSampledDomain(Unit<U> unit, int depth, double frequency, double start) {
    this.unit = requireNonNull(unit);
    this.depth = depth;
    this.frequency = frequency;
    this.start = start;
  }

  @Override
  public double getSample(int index) {
    if (index < 0 || index > getDepth())
      throw new ArrayIndexOutOfBoundsException(index);
    return index / getFrequency() + start;
  }

  @Override
  public int getIndexBelow(double xValue) {
    return (int) (xValue * getFrequency());
  }

  @Override
  public Unit<U> getUnit() {
    return unit;
  }

  @Override
  public int getDepth() {
    return depth;
  }

  /**
   * @return The number of samples per unit in the domain
   */
  public double getFrequency() {
    return frequency;
  }
}
