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
package uk.co.saiman.data.function.processing;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ConvolutionTest {
  private static final double[] EMPTY = new double[] {};
  private static final double[] UNIT = new double[] { 1 };
  // private static final double[] ONE = new double[] { 7 };
  private static final double[] TWO = new double[] { 5, 13 };
  private static final double[] THREE = new double[] { 3, 11, 17 };

  @Test
  public void emptyConvolutionVectorTest() {
    assertThrows(IllegalArgumentException.class, () -> Convolution.process(UNIT, EMPTY));
  }

  @Test
  public void emptyDataTest() {
    assertThrows(NegativeArraySizeException.class, () -> Convolution.process(EMPTY, THREE));
  }

  @Test
  public void smallerDataThanConvolutionVectorTest() {
    double[] result = Convolution.process(TWO, THREE);
    double[] resultOffset0 = Convolution.process(TWO, THREE, 0);
    double[] resultOffset1 = Convolution.process(TWO, THREE, 1);
    double[] resultOffset2 = Convolution.process(TWO, THREE, 2);

    assertArrayEquals(EMPTY, result);
    assertArrayEquals(new double[] { 379, 403 }, resultOffset0);
    assertArrayEquals(new double[] { 291, 379 }, resultOffset1);
    assertArrayEquals(new double[] { 155, 291 }, resultOffset2);
  }
}
