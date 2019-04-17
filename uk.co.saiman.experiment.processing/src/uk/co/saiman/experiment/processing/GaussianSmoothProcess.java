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

import static uk.co.saiman.data.function.processing.GaussianSmooth.DEFAULT_STANDARD_DEVIATION;
import static uk.co.saiman.state.Accessor.doubleAccessor;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.function.processing.GaussianSmooth;
import uk.co.saiman.state.MapIndex;
import uk.co.saiman.state.StateMap;

@Component
public class GaussianSmoothProcess implements ProcessingStrategy<GaussianSmooth> {
  private static final MapIndex<Double> STANDARD_DEVIATION = new MapIndex<>(
      "standardDeviation",
      doubleAccessor());

  @Override
  public GaussianSmooth createProcessor() {
    return new GaussianSmooth();
  }

  @Override
  public GaussianSmooth configureProcessor(StateMap state) {
    return new GaussianSmooth(
        state.getOptional(STANDARD_DEVIATION).orElse(DEFAULT_STANDARD_DEVIATION));
  }

  @Override
  public StateMap deconfigureProcessor(GaussianSmooth processor) {
    return StateMap.empty().with(STANDARD_DEVIATION, processor.getStandardDeviation());
  }

  @Override
  public Class<GaussianSmooth> getType() {
    return GaussianSmooth.class;
  }
}
