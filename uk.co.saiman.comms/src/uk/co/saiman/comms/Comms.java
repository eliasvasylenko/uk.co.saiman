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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms;

import uk.co.saiman.observable.ObservableValue;

/**
 * An interface over a comms channel to a piece of hardware.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the controller
 */
public interface Comms<T> {
  /**
   * A description of the status of a comms channel.
   * 
   * @author Elias N Vasylenko
   */
  enum CommsStatus {
    /**
     * The byte channel is currently open. Subsequent invocations of
     * {@link CommsPort#openChannel()} will fail until the previously opened
     * byte channel is closed.
     */
    OPEN,

    /**
     * The byte channel is ready to be opened. This does not guarantee that a
     * subsequent invocation of {@link CommsPort#openChannel()} will succeed, it
     * simply indicates that it is expected to succeed. If invocation fails, the
     * comms channel will enter a failure state and rethrow when trying to
     * resolve the value of {@link Comms#status()}.
     */
    CLOSED,
  }

  /**
   * @return the human-readable name of the hardware interface
   */
  String getName();

  /**
   * @return the current status of the hardware interface
   */
  ObservableValue<CommsStatus> status();

  /**
   * @return the controller type for interacting with the comms interface
   */
  Class<T> getControllerClass();

  /**
   * Open a controller over the comms interface if one is not already open.
   * 
   * @return If the controller was closed, return the newly opened controller,
   *         else return the controller already open.
   */
  T openController();

  /**
   * Attempt to clear all faults and close the comms interface. Any controllers
   * which are open will be made invalid, and will continue to be invalid even
   * if another controller is opened at a later time.
   */
  void reset();

  /**
   * @return the serial port the comms interface is registered to
   */
  CommsPort getPort();
}
