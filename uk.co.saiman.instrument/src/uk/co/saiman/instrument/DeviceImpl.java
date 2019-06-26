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

public abstract class DeviceImpl<T> implements Device<T> {
  private final String name;
  private final ObservableProperty<DeviceStatus> connectionState;
  private final DeviceRegistration registration;

  private final Semaphore semaphore = new Semaphore(1);
  private volatile ControlContextImpl lockedContext;

  public DeviceImpl(String name, Instrument instrument) {
    this.name = name;
    this.connectionState = over(INACCESSIBLE);
    this.registration = instrument.registerDevice(this);
  }

  protected void dispose() {
    synchronized (connectionState) {
      connectionState.set(DISPOSED);
    }
    closeController();
    registration.deregister();
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
      }
    }
    closeController();
  }

  protected void setAccessible() {
    synchronized (connectionState) {
      if (connectionState.isEqual(INACCESSIBLE)) {
        connectionState.set(AVAILABLE);
      }
    }
  }

  protected abstract T createController(ControlContext context);

  protected void destroyController(ControlContext context) {}

  @Override
  public Control<T> acquireControl(long timeout, TimeUnit unit)
      throws TimeoutException,
      InterruptedException {
    if (!semaphore.tryAcquire(timeout, unit)) {
      throw new TimeoutException();
    }
    ControlContextImpl context;
    synchronized (connectionState) {
      if (connectionState.isEqual(DISPOSED)) {
        semaphore.release();
        throw new IllegalStateException();
      }
      connectionState.set(UNAVAILABLE);
      context = new ControlContextImpl();
    }
    try {
      var controller = createController(context);
      lockedContext = context;
      return new ControlImpl(context, controller);
    } catch (Exception e) {
      connectionState.set(INACCESSIBLE);
      context.close();
      context = null;
      semaphore.release();
      throw e;
    }
  }

  private void releaseControl() {
    synchronized (connectionState) {
      try {
        destroyController(lockedContext);
      } finally {
        if (!connectionState.isMatching(s -> s == DISPOSED || s == INACCESSIBLE)) {
          connectionState.set(AVAILABLE);
        }
        lockedContext = null;
        semaphore.release();
      }
    }
  }

  protected void closeController() {
    var lockedController = this.lockedContext;
    if (lockedController != null) {
      lockedController.close();
    }
  }

  protected interface ControlContext {
    void run(Runnable runnable);

    <U> U get(Supplier<U> supplier);
  }

  public class ControlContextImpl implements ControlContext {
    private volatile boolean closed = false;

    public synchronized void close() {
      if (!isClosed()) {
        closed = true;
        releaseControl();
      }
    }

    public boolean isClosed() {
      return closed;
    }

    @Override
    public synchronized void run(Runnable runnable) {
      if (isClosed()) {
        throw new IllegalStateException();
      } else {
        runnable.run();
      }
    }

    @Override
    public synchronized <U> U get(Supplier<U> supplier) {
      if (isClosed()) {
        throw new IllegalStateException();
      } else {
        return supplier.get();
      }
    }
  }

  protected class ControlImpl implements Control<T> {
    private final ControlContextImpl context;
    private final T controller;

    public ControlImpl(ControlContextImpl context, T controller) {
      this.context = context;
      this.controller = controller;
    }

    @Override
    public void close() {
      context.close();
    }

    @Override
    public boolean isClosed() {
      return context.isClosed();
    }

    @Override
    public T getController() {
      return controller;
    }
  }
}
