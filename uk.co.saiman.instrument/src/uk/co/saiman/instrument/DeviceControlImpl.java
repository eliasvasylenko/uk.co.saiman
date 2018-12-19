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

import java.util.concurrent.TimeUnit;

public class DeviceControlImpl<T extends DeviceImpl<?>> implements AutoCloseable {
  private final T device;
  private boolean open;

  public DeviceControlImpl(T device, long timeout, TimeUnit unit) {
    this.device = device;
    try {
      this.open = device.getSemaphore().tryAcquire(timeout, unit);
    } catch (InterruptedException e) {
      this.open = false;
    }
  }

  protected synchronized void assertOpen() {
    if (!open) {
      throw new DeviceControlClosedException();
    }
  }

  protected T getDevice() {
    assertOpen();
    return device;
  }

  @Override
  public synchronized void close() {
    if (open) {
      device.getSemaphore().release();
      open = false;
    }
  }
}
