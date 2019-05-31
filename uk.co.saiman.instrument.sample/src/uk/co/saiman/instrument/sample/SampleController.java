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

import java.util.concurrent.TimeUnit;

import uk.co.saiman.instrument.Controller;

public interface SampleController<T> extends Controller {
  /**
   * Request that the sample be prepared for exchange.
   * <p>
   * Typically the exchange position for a given piece of hardware means that e.g.
   * the sample are is at atmosphere and/or any inlet valves are shut.
   * <p>
   * The device will initially be put into the
   * {@link SampleState#EXCHANGE_REQUESTED} state. The possible states to follow
   * from this request are either {@link SampleState#EXCHANGE_FAILED} or
   * {@link SampleState#EXCHANGE}.
   * <p>
   * Note that a sample exchange is a physical process, and some devices may
   * require physical interaction in order to transition into the exchange state.
   * In this case, this method have no function other than to enter the
   * {@link SampleState#EXCHANGE_REQUESTED} state and thus indicate that the
   * software is ready to continue.
   */
  void requestExchange();

  /**
   * Request readiness for analysis.
   * <p>
   * The device will initially be put into the
   * {@link SampleState#ANALYSIS_REQUESTED} state. The possible states to follow
   * from this request are either {@link SampleState#ANALYSIS_FAILED} or
   * {@link SampleState#ANALYSIS}.
   * 
   * @param location the location to analyze
   */
  void requestReady();

  /**
   * Request analysis at the given sample location.
   * <p>
   * The device will initially be put into the
   * {@link SampleState#ANALYSIS_REQUESTED} state. The possible states to follow
   * from this request are either {@link SampleState#ANALYSIS_FAILED} or
   * {@link SampleState#ANALYSIS}.
   * 
   * @param location the location to analyze
   */
  void requestAnalysis(T location);

  /**
   * Invocation blocks until the previous request is fulfilled, or until a failure
   * state is reached. The failure state may be a result of the request timing
   * out.
   * 
   * @return the state resulting from the previous request, one of
   *         {@link SampleState#EXCHANGE_FAILED}, {@link SampleState#EXCHANGE},
   *         {@link SampleState#ANALYSIS_FAILED}, or {@link SampleState#ANALYSIS}
   */
  SampleState awaitRequest(long timeout, TimeUnit unit);

  /**
   * Invocation blocks until the device enters the analysis state, or until a
   * failure state is reached. The failure state may be a result of the request
   * timing out.
   * 
   * @return the state resulting from the previous request, one of
   *         {@link SampleState#ANALYSIS_FAILED} or {@link SampleState#ANALYSIS}
   */
  SampleState awaitReady(long timeout, TimeUnit unit);

  @Override
  void close();
}
