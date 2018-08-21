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

import uk.co.saiman.function.ThrowingFunction;

public abstract class SimpleCommsController {
  private CommsPort port;
  private CommsChannel channel;
  private CommsException fault;

  protected void activate(CommsPort port) {
    this.port = port;
    setFault(new CommsException("Awaiting connection"));
    new Thread(() -> reset()).start();
  }

  protected synchronized void deactivate() {
    if (channel != null) {
      channel.close();
      channel = null;
      commsClosed();
    }
  }

  protected synchronized void reset() {
    try {
      if (fault != null) {
        fault = null;
      }
      if (channel != null && !channel.isOpen()) {
        channel = null;
        commsClosed();
      }
      if (channel == null) {
        channel = port.openChannel();
        channel.read();
        try {
          commsOpened();
        } catch (Exception e) {
          try {
            channel.close();
            channel = null;
          } catch (Exception e2) {
            e.addSuppressed(e2);
          }
          throw e;
        }
      }

      checkComms();
    } catch (CommsException e) {
      throw setFault(e);
    } catch (Exception e) {
      throw setFault(new CommsException("Problem opening comms", e));
    }
  }

  protected abstract void commsOpened();

  protected abstract void commsClosed();

  protected abstract void checkComms();

  protected synchronized CommsException setFault(CommsException commsException) {
    this.fault = commsException;
    return commsException;
  }

  public CommsPort getPort() {
    return port;
  }

  protected synchronized <U> U useChannel(ThrowingFunction<CommsChannel, U, Exception> action) {
    try {
      if (fault != null)
        throw fault;

      if (channel != null && !channel.isOpen()) {
        channel = null;
        commsClosed();
      }
      if (channel == null)
        throw new CommsException("Port is closed");

      return action.apply(channel);
    } catch (CommsException e) {
      throw e;
    } catch (Exception e) {
      throw new CommsException("Problem transferring data", e);
    }
  }
}
