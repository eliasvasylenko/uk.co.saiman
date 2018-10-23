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
 * This file is part of uk.co.saiman.experiment.processing.
 *
 * uk.co.saiman.experiment.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.function.processing;

import static uk.co.saiman.data.function.processing.Convolution.DomainModification.EXTENDING;
import static uk.co.saiman.data.function.processing.DataProcessor.arrayProcessor;

import java.util.Arrays;
import java.util.Objects;

import javax.measure.Quantity;

import uk.co.saiman.data.function.SampledContinuousFunction;

public class Convolution implements DataProcessor {
  public enum DomainModification {
    EXTENDING, BOUNDED;
  }

  public static final double[] NO_OP = new double[] { 1 };

  private final double[] vector;
  private final int offset;
  private final DomainModification domainModification;
  private final DataProcessor component;

  public Convolution() {
    this(NO_OP, 0, EXTENDING);
  }

  public Convolution(double[] vector, int offset, DomainModification domainModification) {
    this.vector = vector;
    this.offset = offset;
    this.domainModification = domainModification;
    this.component = domainModification == EXTENDING
        ? arrayProcessor(data -> process(data, vector, offset), offset)
        : arrayProcessor(data -> process(data, vector), offset);
  }

  public Convolution withConvolutionVector(double[] vector) {
    return new Convolution(vector, offset, domainModification);
  }

  public Convolution withConvolutionVectorOffset(int offset) {
    return new Convolution(vector, offset, domainModification);
  }

  public Convolution withConvolutionVector(double[] vector, int offset) {
    return new Convolution(vector, offset, domainModification);
  }

  public double[] getConvolutionVector() {
    return vector;
  }

  public int getConvolutionVectorOffset() {
    return offset;
  }

  public Convolution withDomainModification(DomainModification domainModification) {
    return new Convolution(vector, offset, domainModification);
  }

  public DomainModification getDomainModification() {
    return domainModification;
  }

  @Override
  public <UD extends Quantity<UD>, UR extends Quantity<UR>> SampledContinuousFunction<UD, UR> process(
      SampledContinuousFunction<UD, UR> data) {
    return component.process(data);
  }

  public static double[] process(double[] data, double[] convolutionVector) {
    return processImpl(data, convolutionVector, -1);
  }

  public static double[] process(double[] data, double[] convolutionVector, int offset) {
    if (offset < 0 || offset >= convolutionVector.length)
      throw new IndexOutOfBoundsException();
    return processImpl(data, convolutionVector, offset);
  }

  private static double[] processImpl(double[] data, double[] convolutionVector, int offset) {
    if (convolutionVector.length == 0)
      throw new IllegalArgumentException();

    int size = data.length;
    if (offset < 0) {
      size += 1 - convolutionVector.length;
      offset = 0;
      if (size < 0)
        throw new NegativeArraySizeException();
    }

    double[] convoluted = new double[size];

    for (int i = 0; i < convolutionVector.length; i++) {
      if (convolutionVector[i] != 0) {
        for (int j = 0; j < size; j++) {
          int dataIndex = j + i - offset;
          dataIndex = dataIndex < 0 ? 0 : dataIndex >= data.length ? data.length - 1 : dataIndex;
          convoluted[j] += data[dataIndex] * convolutionVector[i];
        }
      }
    }
    return convoluted;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof Convolution))
      return false;
    if (obj.getClass() != getClass())
      return false;

    Convolution that = (Convolution) obj;
    return Arrays.equals(this.vector, that.vector)
        && this.offset == that.offset
        && this.domainModification == that.domainModification;
  }

  @Override
  public int hashCode() {
    return Objects.hash(vector, offset, domainModification);
  }
}
