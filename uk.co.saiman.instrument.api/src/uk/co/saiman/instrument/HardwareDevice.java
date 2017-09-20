/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.api.
 *
 * uk.co.saiman.instrument.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument;

import java.util.Optional;

import uk.co.saiman.observable.ObservableValue;

public interface HardwareDevice {
  /**
   * @return the human-readable and localized name of the device
   */
  String getName();

  /**
   * Invoked by the controlling instrument upon registration. If participants
   * throw an exception from this invocation, the registration will fail.
   * 
   * @param instrument
   *          The instrument to participate with.
   */
  void setInstrument(Instrument instrument);

  /**
   * Invoked by the controlling instrument upon deregistration.
   */
  void unsetInstrument();

  Optional<Instrument> getInstrument();

  /**
   * Get an observable value over the state of the connection to the hardware
   * device.
   * <p>
   * The value may be set to a {@link ObservableValue#getProblem() problem
   * state}.
   * 
   * @return the connection state
   */
  ObservableValue<HardwareConnection> connectionState();
}
