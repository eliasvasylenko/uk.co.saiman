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

import java.util.Arrays;
import java.util.function.Function;

import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * A mutable, array backed implementation of {@link SampledContinuousFunction}.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public class ArraySampledContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
    implements SampledContinuousFunction<UD, UR> {
  private final SampledDomain<UD> domain;
  private final Unit<UR> rangeUnit;

  private final SampledRange<UR> range;

  /**
   * Instantiate with the given number of samples, values, and intensities.
   * Arrays are copied into the function, truncated to the sample length given,
   * or padded with 0s.
   * 
   * @param domain
   *          the domain of the function
   * @param rangeUnit
   *          the units of measurement of values in the range
   * @param intensities
   *          The Y values of the samples, in the codomain
   */
  public ArraySampledContinuousFunction(
      SampledDomain<UD> domain,
      Unit<UR> rangeUnit,
      double[] intensities) {
    this.domain = domain;
    this.rangeUnit = rangeUnit;
    /*
     * TODO sort values
     */
    double[] intensitiesCopy = Arrays.copyOf(intensities, domain.getDepth());
    this.range = createDefaultRange(i -> intensitiesCopy[i]);
  }

  protected SampledRange<UR> createDefaultRange(Function<Integer, Double> intensityAtIndex) {
    return new SampledRange<UR>(this) {
      @Override
      public Unit<UR> getUnit() {
        return rangeUnit;
      }

      @Override
      public int getDepth() {
        return domain().getDepth();
      }

      @Override
      public double getSample(int index) {
        return intensityAtIndex.apply(index);
      }
    };
  }

  @Override
  public SampledDomain<UD> domain() {
    return domain;
  }

  @Override
  public SampledRange<UR> range() {
    return range;
  }

  @Override
  public int getDepth() {
    return domain().getDepth();
  }
}
