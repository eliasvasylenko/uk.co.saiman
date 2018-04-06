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

import javax.measure.Quantity;

/**
 * A partial-implementation of {@link ContinuousFunction} for sampled continua.
 * The model is as a sequence of (X, Y) points, with (X) increasing in the
 * domain with each index, starting at 0.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public interface SampledContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
    extends ContinuousFunction<UD, UR> {
  @Override
  SampledDomain<UD> domain();

  @Override
  SampledRange<UR> range();

  /**
   * Find the number of samples in the continuum.
   * 
   * @return The depth of the sampled continuum.
   */
  int getDepth();

  @Override
  default double sample(double xPosition) {
    xPosition = domain().getInterval().getConfined(xPosition);

    int indexBelow = domain().getIndexBelow(xPosition);
    int indexAbove = domain().getIndexAbove(xPosition);

    if (indexBelow < 0)
      indexBelow = 0;
    if (indexAbove < 0)
      indexAbove = 0;
    if (indexBelow >= getDepth())
      indexBelow = getDepth() - 1;
    if (indexAbove >= getDepth())
      indexAbove = getDepth() - 1;

    double yBelow = range().getSample(indexBelow);
    double yAbove = range().getSample(indexAbove);

    double xBelow = domain().getSample(indexBelow);
    double xAbove = domain().getSample(indexAbove);

    if (xBelow == xAbove || xPosition == xBelow) {
      return yBelow;
    } else {
      return yBelow + (yAbove - yBelow) * (xPosition - xBelow) / (xAbove - xBelow);
    }
  }

  @Override
  default SampledContinuousFunction<UD, UR> resample(SampledDomain<UD> resolvableSampleDomain) {
    if (getDepth() <= 2) {
      return this;
    }

    int[] indices;
    double[] values;
    double[] intensities;
    int count;

    /*
     * Prepare significant indices
     */
    indices = new int[resolvableSampleDomain.getDepth() * 4 + 8];
    count = 0;

    int indexFrom = domain().getIndexBelow(resolvableSampleDomain.getInterval().getLeftEndpoint());
    if (indexFrom < 0) {
      indexFrom = 0;
    }
    int indexTo = domain().getIndexAbove(resolvableSampleDomain.getInterval().getRightEndpoint());
    if (indexTo >= getDepth() || indexTo < 0) {
      indexTo = getDepth() - 1;
    }

    indices[count++] = indexFrom;

    int resolvedUnit = 0;
    double resolvedUnitX = resolvableSampleDomain.getInterval().getLeftEndpoint();

    int lastIndex;

    double minY;
    double maxY;

    int minIndex;
    int maxIndex;

    double sampleX;
    double sampleY;

    lastIndex = minIndex = maxIndex = indexFrom;
    minY = maxY = range().getSample(lastIndex);
    for (int index = indexFrom + 1; index < indexTo; index++) {
      /*
       * Get sample location at index
       */
      sampleX = domain().getSample(index);
      sampleY = range().getSample(index);

      /*
       * Check if passed resolution boundary (or last position)
       */
      if (sampleX > resolvedUnitX || index + 1 == indexTo) {
        /*
         * Move to next resolution boundary
         */

        do {
          resolvedUnit++;
          resolvedUnitX = resolvableSampleDomain.getSample(resolvedUnit);
        } while (resolvedUnitX < sampleX);

        /*
         * Add indices of minimum and maximum y encountered in boundary span
         */
        if (sampleY < minY) {
          minIndex = -1;
        } else if (sampleY > maxY) {
          maxIndex = -1;
        }

        if (minIndex > 0) {
          if (maxIndex > 0) {
            if (maxIndex > minIndex) {
              indices[count++] = minIndex;
              indices[count++] = maxIndex;
            } else {
              indices[count++] = maxIndex;
              indices[count++] = minIndex;
            }
          } else {
            indices[count++] = minIndex;
          }
        } else if (maxIndex > 0) {
          indices[count++] = maxIndex;
        }

        if (index > lastIndex) {
          indices[count++] = index;
        }
        lastIndex = index + 1;
        indices[count++] = lastIndex;

        minIndex = -1;
        maxIndex = -1;
      } else if (index > lastIndex) {
        /*
         * Check for Y range expansion
         */
        if (maxIndex == -1 || sampleY > maxY) {
          maxY = sampleY;
          maxIndex = index;
        } else if (minIndex == -1 || sampleY < minY) {
          minY = sampleY;
          minIndex = index;
        }
      }
    }

    /*
     * Prepare significant values
     */
    values = new double[count];
    for (int i = 0; i < count; i++) {
      values[i] = domain().getSample(indices[i]);
    }

    /*
     * Prepare significant intensities
     */
    intensities = new double[count];
    for (int i = 0; i < count; i++) {
      intensities[i] = range().getSample(indices[i]);
    }

    /*
     * Prepare linearisation
     */
    return new ArraySampledContinuousFunction<>(
        new IrregularSampledDomain<>(domain().getUnit(), values),
        range().getUnit(),
        intensities);
  }
}
