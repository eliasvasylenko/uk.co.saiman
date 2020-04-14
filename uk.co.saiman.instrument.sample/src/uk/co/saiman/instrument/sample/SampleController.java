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
   * Withdraw any current request made of the device.
   */
  void withdrawRequest();

  /**
   * Request that the sample be prepared for exchange. If the device is already in
   * the requested state, do nothing.
   * <p>
   * Typically the exchange position for a given piece of hardware means that e.g.
   * the sample are is at atmosphere and/or any inlet valves are shut.
   * <p>
   * Note that a sample exchange is a physical process, and some devices may
   * require physical interaction in order to transition into the exchange state.
   * In this case, this method can only signal intent and may not directly
   * initiate an exchange.
   * <p>
   * If the actual sample exchange process is decoupled from the sample device
   * controller then the UI should present this request to the user and ask for
   * their intervention.
   */
  void requestExchange();

  /**
   * Request readiness for analysis. If the device is already in the requested
   * state, do nothing.
   */
  void requestReady();

  /**
   * Request analysis at the given sample location. If the device is already in
   * the requested state, do nothing.
   * <p>
   * The invocation may fail with an exception if the device is not in the ready
   * state or the analysis state, or if the requested location is not reachable.
   * <p>
   * The device implementation may choose to hold an analysis location until some
   * internal condition is cleared, for example to ensure that the device can
   * safely participate in some collaborate process with other devices. This means
   * that while the sample is in the analysis state any requests for new analysis
   * locations or for exchange may not be immediately fulfilled.
   * 
   * @param position
   *          the location to analyze
   */
  void requestAnalysis(T position);

  default void request(RequestedSampleState<T> state) {
    if (state instanceof Analysis<?>) {
      requestAnalysis(((Analysis<T>) state).position());
    } else if (state instanceof Ready<?>) {
      requestReady();
    } else if (state instanceof Exchange<?>) {
      requestExchange();
    }
  }

  /**
   * Invocation blocks until the previous request is fulfilled, or until a failure
   * state is reached. The failure state may be a result of the request timing
   * out.
   * 
   * @return the state resulting from the previous request, one of {@link Failed},
   *         {@link Exchange}, or {@link Ready}
   */
  SampleState<T> awaitRequest(long timeout, TimeUnit unit);

  /**
   * Invocation blocks until the device enters the analysis state, or until a
   * failure state is reached. The failure state may be a result of the request
   * timing out.
   * 
   * @return the state resulting from the previous request, one of {@link Failed}
   *         or {@link Ready}
   */
  SampleState<T> awaitReady(long timeout, TimeUnit unit);
}
