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

import static uk.co.saiman.data.function.processing.DataProcessor.arrayProcessor;
import static uk.co.saiman.experiment.persistence.Accessor.booleanAccessor;
import static uk.co.saiman.experiment.persistence.Accessor.doubleAccessor;
import static uk.co.saiman.experiment.persistence.Accessor.intAccessor;

import java.util.stream.DoubleStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.persistence.Accessor;
import uk.co.saiman.experiment.persistence.StateMap;
import uk.co.saiman.properties.PropertyLoader;

@Component
public class Convolution implements Processor<Convolution> {
  private static final Accessor<double[], ?> VECTOR = doubleAccessor("vector")
      .toStreamAccessor()
      .map(s -> s.mapToDouble(e -> e).toArray(), a -> DoubleStream.of(a).mapToObj(e -> e));
  private static final Accessor<Integer, ?> CENTRE = intAccessor("centre");
  private static final Accessor<Boolean, ?> EXTEND = booleanAccessor("extend");

  @Reference
  PropertyLoader propertyLoader;

  private final StateMap state;

  public Convolution() {
    this(StateMap.empty());
  }

  public Convolution(StateMap state) {
    this.state = state
        .withDefault(VECTOR, new double[] { 1 })
        .withDefault(CENTRE, 0)
        .withDefault(EXTEND, true);
  }

  @Override
  public String getName() {
    return propertyLoader.getProperties(ProcessingProperties.class).convolutionProcessor().get();
  }

  @Override
  public Convolution withState(StateMap state) {
    return new Convolution(state);
  }

  @Override
  public StateMap getState() {
    return state;
  }

  public Convolution withConvolutionVector(double[] vector) {
    return withState(state.with(VECTOR, vector));
  }

  public Convolution withConvolutionVectorCentre(int centre) {
    return withState(state.with(CENTRE, centre));
  }

  public Convolution withConvolutionVector(double[] vector, int centre) {
    return withState(state.with(VECTOR, vector).with(CENTRE, centre));
  }

  public double[] getConvolutionVector() {
    return state.get(VECTOR);
  }

  public int getConvolutionVectorCentre() {
    return state.get(CENTRE);
  }

  public Convolution withDomainExtended(boolean extend) {
    return withState(state.withDefault(EXTEND, extend));
  }

  public boolean isDomainExtended() {
    return state.get(EXTEND);
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
  public Class<Convolution> getType() {
    return Convolution.class;
  }
}
