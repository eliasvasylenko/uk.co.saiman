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

import static uk.co.saiman.comms.Comms.CommsStatus.CLOSED;
import static uk.co.saiman.comms.Comms.CommsStatus.OPEN;

import java.io.IOException;
import java.nio.channels.ByteChannel;

import uk.co.strangeskies.function.ThrowingFunction;
import uk.co.strangeskies.observable.MissingValueException;
import uk.co.strangeskies.observable.ObservablePropertyImpl;
import uk.co.strangeskies.observable.ObservableValue;

/**
 * A simple immutable class defining named addresses for pushing and requesting
 * bytes to and from a {@link CommsPort comms channel}.
 * 
 * @author Elias N Vasylenko
 */
public abstract class SimpleComms<T> implements Comms<T> {
  private final String name;

  private CommsPort comms;
  private CommsChannel channel;

  private final ObservablePropertyImpl<CommsStatus> status;

  private SimpleController<T> controller;

  /**
   * Initialize an empty address space.
   */
  public SimpleComms(String name) {
    this.name = name;

    status = new ObservablePropertyImpl<>((a, b) -> a == b, CLOSED);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ObservableValue<CommsStatus> status() {
    return status;
  }

  protected synchronized CommsException setFault(CommsException commsException) {
    status.setProblem(commsException);
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
    status.set(CLOSED);
  }

  @Override
  public synchronized T openController() {
    status().tryGetProblem().ifPresent(t -> {
      reset();
    });

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
    }

    return controller.getController();
  }

  protected abstract SimpleController<T> createController();

  @Override
  public synchronized void reset() {
    if (!status().isEqual(CLOSED)) {
      try {
        comms.close();
        controller.closeController();
        controller = null;
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
      }

      return action.apply(channel);
    } catch (MissingValueException e) {
      throw new CommsException("Problem opening comms", e.getCause());
    } catch (Exception e) {
      throw new CommsException("Problem transferring data", e);
    }
  }
}
