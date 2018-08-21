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

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;
import static uk.co.saiman.experiment.state.Accessor.doubleAccessor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;
import uk.co.saiman.properties.PropertyLoader;

@Component
public class GaussianSmooth implements Processor<GaussianSmooth> {
  private static final PropertyAccessor<Double> STANDARD_DEVIATION = doubleAccessor(
      "standardDeviation");
  private static final int BOX_ITERATIONS = 5;

  @Reference
  PropertyLoader propertyLoader;

  private final StateMap state;

  public GaussianSmooth() {
    this(StateMap.empty());
  }

  public GaussianSmooth(StateMap state) {
    this.state = state.withDefault(STANDARD_DEVIATION, () -> 10d);
  }

  @Override
  public String getName() {
    return propertyLoader.getProperties(ProcessingProperties.class).gaussianSmoothProcessor().get();
  }

  public double getStandardDeviation() {
    return state.get(STANDARD_DEVIATION);
  }

  public GaussianSmooth withStandardDeviation(double standardDeviation) {
    return withState(state.with(STANDARD_DEVIATION, standardDeviation));
  }

  @Override
  public StateMap getState() {
    return state;
  }

  @Override
  public GaussianSmooth withState(StateMap state) {
    return new GaussianSmooth(state);
  }

  @Override
  public DataProcessor getProcessor() {
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

    return DataProcessor.arrayProcessor(data -> {
      data = data.clone();

      for (int i = 0; i < lowerIterations; i++)
        BoxFilter.applyInPlace(data, lowerBoxWidth);

      for (int i = 0; i < higherIterations; i++)
        BoxFilter.applyInPlace(data, higherBoxWidth);

      return data;
    }, 0);
  }

  @Override
  public Class<GaussianSmooth> getType() {
    return GaussianSmooth.class;
  }
}
