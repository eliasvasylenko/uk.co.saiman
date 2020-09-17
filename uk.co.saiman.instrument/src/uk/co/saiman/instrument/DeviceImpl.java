/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static uk.co.saiman.instrument.ControllerStatus.AVAILABLE;
import static uk.co.saiman.instrument.ControllerStatus.UNAVAILABLE;
import static uk.co.saiman.instrument.DeviceStatus.ACCESSIBLE;
import static uk.co.saiman.instrument.DeviceStatus.DISPOSED;
import static uk.co.saiman.instrument.DeviceStatus.INACCESSIBLE;
import static uk.co.saiman.observable.ObservableProperty.over;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.saiman.locking.Lock;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public abstract class DeviceImpl<T extends Controller> implements Device {
  private final ObservableProperty<DeviceStatus> status;
  private final ObservableProperty<ControllerStatus> controllerStatus;

  private final Semaphore deviceSemaphore = new Semaphore(1);
  private volatile ControlContextImpl lockedContext;

  public DeviceImpl() {
    this.status = over(INACCESSIBLE);
    this.controllerStatus = over(AVAILABLE);
  }

  @Override
  public ObservableValue<DeviceStatus> status() {
    return status;
  }

  @Override
  public ObservableValue<ControllerStatus> controllerStatus() {
    return controllerStatus;
  }

  protected void setDisposed() {
    synchronized (status) {
      status.set(DISPOSED);
    }
  }

  protected void setInaccessible() {
    synchronized (status) {
      if (!status.isValueEqual(DISPOSED)) {
        status.set(INACCESSIBLE);
      }
    }
  }

  protected void setAccessible() {
    synchronized (status) {
      if (!status.isValueEqual(DISPOSED)) {
        status.set(ACCESSIBLE);
      }
    }
  }

  protected abstract T createController(ControlContext context, long timeout, TimeUnit unit)
      throws TimeoutException, InterruptedException;

  protected void destroyController(ControlContext context) {}

  @Override
  public T acquireControl(long timeout, TimeUnit unit)
      throws TimeoutException, InterruptedException {
    if (!deviceSemaphore.tryAcquire(timeout, unit)) {
      throw new TimeoutException();
    }

    try {
      if (status.isValueEqual(DISPOSED)) {
        throw new IllegalStateException();
      }

      try {
        System.out.println("     @! acquire! " + this);
        lockedContext = new ControlContextImpl();

        var controller = createController(lockedContext, timeout, unit);

        controllerStatus.set(UNAVAILABLE);
        status.set(ACCESSIBLE);

        return controller;

      } catch (Exception e) {
        System.out.println("     @! fail! " + this);
        lockedContext = null;

        controllerStatus.set(AVAILABLE);
        status.set(INACCESSIBLE);

        throw e;
      }
    } catch (Exception e) {
      deviceSemaphore.release();

      throw e;
    }
  }

  private void releaseControl(ControlContextImpl control) {
    if (lockedContext == control) {
      try {
        controllerStatus.set(AVAILABLE);
        status.set(INACCESSIBLE);

        destroyController(lockedContext);
      } finally {
        System.out.println("     @! release! " + this);
        lockedContext = null;
        deviceSemaphore.release();
      }
    }
  }

  public interface ControlContext {
    void close();

    boolean isOpen();

    Lock acquireLock();
  }

  protected class ControlContextImpl implements ControlContext {
    private final Semaphore controllerSemaphore = new Semaphore(1);

    @Override
    public void close() {
      /*
       * TODO if lock is acquired by any run/get, interrupt them?
       */
      controllerSemaphore.acquireUninterruptibly();
      try {
        releaseControl(this);
      } finally {
        controllerSemaphore.release();
      }
    }

    @Override
    public boolean isOpen() {
      controllerSemaphore.acquireUninterruptibly();
      try {
        return lockedContext == this;
      } finally {
        controllerSemaphore.release();
      }
    }

    @Override
    public Lock acquireLock() {
      Lock unlock = controllerSemaphore::release;
      try {
        controllerSemaphore.acquire();
      } catch (InterruptedException e) {
        throw new DeviceException(e);
      }
      if (lockedContext != this) {
        unlock.close();
        throw new DeviceException("Controller is stale");
      }
      if (status.isValueEqual(DISPOSED)) {
        unlock.close();
        throw new DeviceException("Device has been disposed");
      }
      return unlock;
    }
  }
}
