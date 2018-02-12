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

import uk.co.saiman.observable.ObservableValue;

/**
 * Typically this interface should be extended to provide interfaces over
 * general classes of hardware such that alternative hardware can be
 * substituted.
 * 
 * @author Elias N Vasylenko
 */
public interface Device {
  /**
   * @return the human-readable and localized name of the device
   */
  String getName();

  /**
   * Devices should only return an instrument they are added to, and should only
   * add themselves to a single instrument.
   * 
   * @return the instrument this device is a part of, or null if it is not a part
   *         of an instrument
   */
  Instrument getInstrument();

  /**
   * Get an observable value over the state of the connection to the hardware
   * device.
   * 
   * @return the connection state
   */
  ObservableValue<DeviceConnection> connectionState();
}
