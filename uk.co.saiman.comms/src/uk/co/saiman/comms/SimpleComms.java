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

import static uk.co.saiman.comms.Comms.CommsStatus.CLOSED;
import static uk.co.saiman.comms.Comms.CommsStatus.FAULT;
import static uk.co.saiman.comms.Comms.CommsStatus.OPEN;

import java.io.IOException;
import java.nio.channels.ByteChannel;

import uk.co.saiman.function.ThrowingFunction;
import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

/**
 * A simple immutable class defining named addresses for pushing and requesting
 * bytes to and from a {@link CommsPort comms channel}.
 * 
 * @author Elias N Vasylenko
 */
public abstract class SimpleComms<T> implements Comms<T> {
  private CommsPort comms;
  private CommsChannel channel;

  private CommsException fault;
  private final ObservablePropertyImpl<CommsStatus> status;

  private SimpleController<T> controller;

  /**
   * Initialize an empty address space.
   */
  public SimpleComms() {
    status = new ObservablePropertyImpl<>(CLOSED);
  }

  @Override
  public ObservableValue<CommsStatus> status() {
    return status;
  }

  protected synchronized CommsException setFault(CommsException commsException) {
    this.fault = commsException;
    status.set(FAULT);
    return commsException;
  }

  protected abstract void checkComms();

  protected synchronized void unsetComms() throws IOException {
    try {
      reset();
    } catch (Exception e) {}
    this.comms = null;
    setFault(new CommsException("No port configured"));
  }

  protected synchronized void setComms(CommsPort comms) throws IOException {
    try {
      reset();
    } catch (Exception e) {}
    this.comms = comms;
    fault = null;
    status.set(CLOSED);
  }

  @Override
  public synchronized T openController() {
    switch (status().get()) {
    case OPEN:
      break;

    case CLOSED:
      try {
        channel = comms.openChannel();
        status.set(OPEN);
        checkComms();
      } catch (CommsException e) {
        throw setFault(e);
      } catch (Exception e) {
        throw setFault(new CommsException("Problem opening comms", e));
      }
      controller = createController();
      break;

    case FAULT:
      throw fault;
    }

    return controller.getController();
  }

  protected abstract SimpleController<T> createController();

  @Override
  public synchronized void reset() {
    if (!status().isEqual(CLOSED)) {
      try {
        comms.close();
        if (controller != null) {
          controller.closeController();
          controller = null;
        }
        fault = null;
        status.set(CLOSED);
      } catch (CommsException e) {
        throw setFault(e);
      } catch (Exception e) {
        throw setFault(new CommsException("Problem closing comms", e));
      }
    }
  }

  @Override
  public CommsPort getPort() {
    return comms;
  }

  protected synchronized <U> U useChannel(ThrowingFunction<ByteChannel, U, Exception> action) {
    try {
      switch (status().get()) {
      case OPEN:
        return action.apply(channel);

      case CLOSED:
        throw new CommsException("Port is closed");

      case FAULT:
        throw fault;
      }

      return action.apply(channel);
    } catch (Exception e) {
      throw new CommsException("Problem transferring data", e);
    }
  }
}
