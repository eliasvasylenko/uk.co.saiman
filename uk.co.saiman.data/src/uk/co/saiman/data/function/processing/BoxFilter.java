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

import static java.util.Arrays.fill;
import static uk.co.saiman.data.function.processing.DataProcessor.arrayProcessor;

import javax.measure.Quantity;

import uk.co.saiman.data.function.SampledContinuousFunction;

public class BoxFilter implements DataProcessor {
  public static final int NO_OP = 1;

  private final int width;
  private final DataProcessor component;

  public BoxFilter() {
    this(NO_OP);
  }

  public BoxFilter(int width) {
    this.width = width;
    this.component = arrayProcessor(data -> {
      data = data.clone();
      applyInPlace(data, width);
      return data;
    }, width / 2);
  }

  public int getWidth() {
    return width;
  }

  public BoxFilter withWidth(int width) {
    return new BoxFilter(width);
  }

  public static void applyInPlace(double[] data, int boxWidth) {
    if (boxWidth <= 0)
      throw new IllegalArgumentException();
    if (boxWidth == 1)
      return;

    int headWidth = boxWidth / 2;
    int tailWidth = boxWidth - headWidth - 1; // depends whether even or odd
    double[] headMemory = new double[headWidth];
    fill(headMemory, data[0]);

    double runningTotal = data[0] * headWidth;
    for (int i = 0; i < tailWidth; i++) {
      int index = i;
      if (index >= data.length)
        index = data.length - 1;
      runningTotal += data[index];
    }

    data[0] = runningTotal / boxWidth;

    for (int i = 1; i < data.length; i++) {
      int headIndex = i % headWidth;
      int tailIndex = i + tailWidth;
      if (tailIndex >= data.length)
        tailIndex = data.length - 1;

      runningTotal += data[tailIndex] - headMemory[headIndex];

      headMemory[headIndex] = data[i];
      data[i] = runningTotal / boxWidth;
    }
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
    if (!(obj instanceof BoxFilter))
      return false;
    if (obj.getClass() != getClass())
      return false;

    BoxFilter that = (BoxFilter) obj;
    return this.width == that.width;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(width);
  }
}
