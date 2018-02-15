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

import java.io.Closeable;

/**
 * Some sort of comms interface which can be opened as a byte channel.
 * 
 * @author Elias N Vasylenko
 */
public interface CommsPort {
  String getName();

  /**
   * @return a human readable name for the channel, where available
   */
  @Override
  String toString();

  /**
   * @return the current status of the channel
   */
  boolean isOpen();

  /**
   * Kill any open {@link #openChannel() channel} or {@link #openStream() stream}
   * and reset the comms system to it's basic disconnected state. Generally an
   * open port should be closed by invoking {@link Closeable#close()} on the open
   * channel or stream, so as to allow the user to manage their own connection.
   * This method is only provided to deal with unresponsive or uncooperative
   * users.
   */
  void kill();

  /**
   * Open a byte channel over the comms interface. The caller is responsible for
   * closing the channel, and has exclusive access to it until this time.
   * Successive invocations will fail until the previously returned channel is
   * closed.
   * 
   * @return the opened byte channel
   */
  CommsChannel openChannel();

  default CommsStream openStream() {
    return openStream(0);
  }

  CommsStream openStream(int packetSize);
}
