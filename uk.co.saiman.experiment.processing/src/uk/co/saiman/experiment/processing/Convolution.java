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
package uk.co.saiman.experiment.processing;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static uk.co.saiman.data.function.processing.DataProcessor.arrayProcessor;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.processing.Convolution.State;
import uk.co.saiman.property.Property;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class Convolution implements ProcessorType<State> {
  protected static final double[] NO_OP = new double[] { 1 };

  private static final String VECTOR_KEY = "vector";
  private static final String CENTRE_KEY = "centre";
  private static final String EXTEND_KEY = "extend";

  @Reference
  PropertyLoader propertyLoader;

  @Override
  public String getName() {
    return propertyLoader.getProperties(ProcessingProperties.class).convolutionProcessor().get();
  }

  @Override
  public State configure(PersistedState state) {
    return new State(state);
  }

  public class State extends ProcessorState {
    private final Property<double[]> vector;
    private final Property<Integer> centre;
    private final Property<Boolean> extend;

    public State(PersistedState state) {
      super(Convolution.this, state);
      vector = state
          .forString(VECTOR_KEY)
          .map(
              v -> stream(v.split(",")).mapToDouble(Double::parseDouble).toArray(),
              v -> stream(v).mapToObj(Double::toString).collect(joining(",")))
          .setDefault(() -> NO_OP);
      centre = state
          .forString(CENTRE_KEY)
          .map(Integer::parseInt, Objects::toString)
          .setDefault(() -> 0);
      extend = state
          .forString(EXTEND_KEY)
          .map(Boolean::parseBoolean, Objects::toString)
          .setDefault(() -> true);
    }

    public void setConvolutionVector(double[] vector, int centre) {
      this.vector.set(vector);
      this.centre.set(centre);
    }

    public double[] getConvolutionVector() {
      return vector.get();
    }

    public int getConvolutionVectorCentre() {
      return centre.get();
    }

    public void setDomainExtended(boolean extend) {
      this.extend.set(extend);
    }

    public boolean isDomainExtended() {
      return extend.get();
    }

    @Override
    public DataProcessor getProcessor() {
      double[] vector = getConvolutionVector();
      return isDomainExtended()
          ? arrayProcessor(
              data -> process(data, vector, getConvolutionVectorCentre()),
              getConvolutionVectorCentre())
          : arrayProcessor(data -> process(data, vector), getConvolutionVectorCentre());
    }
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
}
