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
 * This file is part of uk.co.saiman.instrument.sample.
 *
 * uk.co.saiman.instrument.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.sample;

public interface SampleControl<T> extends AutoCloseable {
  /**
   * Request that the sample be prepared for exchange.
   * <p>
   * Invocation blocks until the sample is prepared or a stable failure state is
   * reached.
   * 
   * Typically the exchange position for a given piece of hardware means that e.g.
   * the sample are is at atmosphere and/or any inlet valves are shut.
   * 
   * @return the resulting state, either {@link SampleState#EXCHANGE_FAILED} or
   *         {@link SampleState#EXCHANGE}
   */
  SampleState requestExchange();

  /**
   * Request that the sample be prepared for analysis.
   * <p>
   * Invocation blocks until the sample is prepared or a stable failure state is
   * reached.
   * 
   * @return the resulting state, either
   *         {@link SampleState#ANALYSIS_LOCATION_FAILED} or
   *         {@link SampleState#ANALYSIS}
   */
  SampleState requestAnalysis();

  /**
   * Request analysis at the given sample location.
   * <p>
   * Invocation blocks until the sample is prepared or a stable failure state is
   * reached.
   * 
   * @param location the location to analyze
   * @return the resulting state, either
   *         {@link SampleState#ANALYSIS_LOCATION_FAILED} or
   *         {@link SampleState#ANALYSIS}
   */
  SampleState requestAnalysisLocation(T location);

  @Override
  void close();
}
