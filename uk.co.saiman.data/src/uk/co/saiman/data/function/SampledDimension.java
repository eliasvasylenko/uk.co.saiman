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

import javax.measure.Quantity;

/**
 * A dimension of the sample space of a sampled continuous function.
 * 
 * @author Elias N Vasylenko
 *
 * @param <U> the unit of measurement of values in this dimension
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
   * @param index The sample index.
   * @return The X value of the sample at the given index.
   */
  double getSample(int index);

  default double[] toArray() {
    int depth = getDepth();
    double[] array = new double[depth];
    for (int i = 0; i < depth; i++) {
      array[i] = getSample(i);
    }
    return array;
  }
}
