/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.data.function.processing;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

import javax.measure.Quantity;

import uk.co.saiman.data.function.SampledContinuousFunction;

public class GaussianSmooth implements DataProcessor {
  public static final double DEFAULT_STANDARD_DEVIATION = 10;

  private static final int BOX_ITERATIONS = 5;

  private final double standardDeviation;
  private final DataProcessor component;

  public GaussianSmooth() {
    this(DEFAULT_STANDARD_DEVIATION);
  }

  public GaussianSmooth(double standardDeviation) {
    this.standardDeviation = standardDeviation;

    /*
     * This is a little dense to properly document in place. For more information,
     * the implementation is based on the report "Fast Almost-Gaussian Filtering" by
     * Peter Kovesi.
     */

    double stdDevSquared12 = (getStandardDeviation() * getStandardDeviation()) * 12d;
    double idealBoxWidth = sqrt((stdDevSquared12 / BOX_ITERATIONS) + 1);

    int lowerBoxWidth = (int) floor(idealBoxWidth / 2) * 2 - 1;
    int lowerIterations = (int) ((BOX_ITERATIONS * (lowerBoxWidth * (lowerBoxWidth + 4) + 3)
        - stdDevSquared12) / (4 * lowerBoxWidth + 4));

    int higherBoxWidth = lowerBoxWidth + 2;
    int higherIterations = BOX_ITERATIONS - lowerIterations;

    this.component = DataProcessor.arrayProcessor(data -> {
      data = data.clone();

      for (int i = 0; i < lowerIterations; i++)
        BoxFilter.applyInPlace(data, lowerBoxWidth);

      for (int i = 0; i < higherIterations; i++)
        BoxFilter.applyInPlace(data, higherBoxWidth);

      return data;
    }, 0);
  }

  public double getStandardDeviation() {
    return standardDeviation;
  }

  public GaussianSmooth withStandardDeviation(double standardDeviation) {
    return new GaussianSmooth(standardDeviation);
  }

  @Override
  public <UD extends Quantity<UD>, UR extends Quantity<UR>> SampledContinuousFunction<UD, UR> process(
      SampledContinuousFunction<UD, UR> data) {
    return component.process(data);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof GaussianSmooth))
      return false;
    if (obj.getClass() != getClass())
      return false;

    GaussianSmooth that = (GaussianSmooth) obj;
    return Double.compare(this.standardDeviation, that.standardDeviation) == 0;
  }

  @Override
  public int hashCode() {
    return Double.hashCode(standardDeviation);
  }
}
