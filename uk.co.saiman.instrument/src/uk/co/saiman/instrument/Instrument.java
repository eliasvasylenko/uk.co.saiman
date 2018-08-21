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
 * This file is part of uk.co.saiman.instrument.
 *
 * uk.co.saiman.instrument is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument;

import java.util.stream.Stream;

import uk.co.saiman.observable.ObservableValue;

/**
 * An instrument is essentially the aggregation of a collection of
 * {@link Device}s, and is responsible for supervising their lifecycles and
 * interactions as part of the collected whole.
 * 
 * @author Elias N Vasylenko
 */
public interface Instrument {
  public static final String INSTRUMENT_CATEGORY = "Instrument";

  void requestOperation();

  void requestStandby();

  ObservableValue<InstrumentLifecycleState> lifecycleState();

  default Stream<? extends Device> getDevices() {
    return getRegistrations().map(DeviceRegistration::getDevice);
  }

  Stream<? extends DeviceRegistration> getRegistrations();

  InstrumentRegistration registerDevice(Device device);
}
