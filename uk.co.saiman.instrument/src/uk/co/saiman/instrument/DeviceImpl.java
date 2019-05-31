/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static uk.co.saiman.instrument.DeviceStatus.AVAILABLE;
import static uk.co.saiman.instrument.DeviceStatus.DISPOSED;
import static uk.co.saiman.instrument.DeviceStatus.INACCESSIBLE;
import static uk.co.saiman.instrument.DeviceStatus.UNAVAILABLE;
import static uk.co.saiman.observable.ObservableProperty.over;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public abstract class DeviceImpl<T extends Controller> implements Device<T> {
  private final String name;
  private final ObservableProperty<DeviceStatus> connectionState;
  private final DeviceRegistration registration;

  private final Semaphore semaphore = new Semaphore(1);
  private volatile T lockedController;

  public DeviceImpl(String name, Instrument instrument) {
    this.name = name;
    this.connectionState = over(INACCESSIBLE);
    this.registration = instrument.registerDevice(this);
  }

  protected void dispose() {
    synchronized (connectionState) {
      connectionState.set(DISPOSED);
      releaseControl();
      registration.deregister();
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public InstrumentRegistration getInstrumentRegistration() {
    return registration.getInstrumentRegistration();
  }

  @Override
  public ObservableValue<DeviceStatus> status() {
    return connectionState;
  }

  protected void setInaccessible() {
    synchronized (connectionState) {
      if (!connectionState.isEqual(DISPOSED)) {
        connectionState.set(INACCESSIBLE);
        releaseControl();
      }
    }
  }

  protected void setAccessible() {
    synchronized (connectionState) {
      if (connectionState.isEqual(INACCESSIBLE)) {
        connectionState.set(AVAILABLE);
      }
    }
  }

  protected abstract T acquireControl(ControlLock lock);

  @Override
  public T acquireControl(long timeout, TimeUnit unit)
      throws TimeoutException,
      InterruptedException {
    if (!semaphore.tryAcquire(timeout, unit)) {
      throw new TimeoutException();
    }
    ControlLock lock;
    synchronized (connectionState) {
      if (connectionState.isEqual(DISPOSED)) {
        semaphore.release();
        throw new IllegalStateException();
      }
      connectionState.set(UNAVAILABLE);
      lock = new ControlLock(this::releaseControl);
    }
    try {
      return lockedController = acquireControl(lock);
    } catch (Exception e) {
      connectionState.set(INACCESSIBLE);
      lock.close();
      lock = null;
      semaphore.release();
      throw e;
    }
  }

  protected void releaseControl() {
    synchronized (connectionState) {
      if (lockedController != null) {
        lockedController.close();
        lockedController = null;
        semaphore.release();
      }
      if (!connectionState.isMatching(s -> s == DISPOSED || s == INACCESSIBLE)) {
        connectionState.set(AVAILABLE);
      }
    }
  }

  public static class ControlLock {
    private volatile Runnable close;

    public ControlLock(Runnable close) {
      this.close = close;
    }

    public synchronized boolean isOpen() {
      return close != null;
    }

    public synchronized void run(Runnable runnable) {
      if (!isOpen()) {
        throw new IllegalStateException();
      } else {
        runnable.run();
      }
    }

    public synchronized <T> T get(Supplier<T> supplier) {
      if (!isOpen()) {
        throw new IllegalStateException();
      } else {
        return supplier.get();
      }
    }

    public synchronized void close() {
      close(() -> {});
    }

    public synchronized void close(Runnable runnable) {
      if (isOpen()) {
        try {
          runnable.run();
        } finally {
          var close = this.close;
          this.close = null;
          close.run();
        }
      }
    }
  }
}
