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
package uk.co.saiman.instrument.stage.composed;

import javax.measure.Quantity;

import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.observable.ObservableValue;

public interface StageAxis<T extends Quantity<T>> {
  ObservableValue<AxisState> axisState();

  /**
   * Initiate a request and return immediately. Throws an exception if the axis is
   * currently attempting to fulfill a previous request (i.e.
   * {@link SampleState#ANALYSIS_LOCATION_REQUESTED} or
   * {@link SampleState#EXCHANGE_REQUESTED}).
   * 
   * @param location
   */
  void requestLocation(Quantity<T> location);

  /**
   * Attempt to abort any request in progress. Is a no-op if no request is being
   * fulfilled, if the request completes regardless, or if the hardware does not
   * support abort.
   */
  void abortRequest();

  ObservableValue<Quantity<T>> actualLocation();
}
