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
 * This file is part of uk.co.saiman.instrument.stage.
 *
 * uk.co.saiman.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.sample;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

/**
 * A hardware devices which manages sample exchange and analysis.
 * 
 * This abstraction should be general enough to be applicable to any sample
 * source with some sort of sample selection/navigation mechanism, e.g. a system
 * of inlet valves or a motor stage.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          The sample location space.
 */
public interface SampleDevice<T> extends Device {
  ObservableValue<SampleState> sampleState();

  /**
   * Generally this should be a straightforward check that the given position is
   * within the {@link #getLowerBound() lower} and {@link #getUpperBound() upper}
   * bounds of the stage, but sometimes the bounds may take different shapes.
   * Implementations of this method should be idempotent and free of side-effects.
   * 
   * @param position
   * @return
   */
  boolean isLocationReachable(T location);

  ObservableProperty<T> requestedLocation();

  ObservableValue<T> actualLocation();

  /**
   * Typically the exchange position for a given piece of hardware means that e.g.
   * the sample are is at atmosphere and/or any inlet valves are shut.
   */
  void requestExchange();

  void requestAnalysis();
}
