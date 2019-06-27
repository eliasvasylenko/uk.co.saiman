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
package uk.co.saiman.instrument.virtual;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import uk.co.saiman.instrument.Controller;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceStatus;
import uk.co.saiman.observable.Disposable;

public class DeviceDependency<V extends Controller> {
  private final Device<V> device;
  private final long acquireTimeout;
  private final TimeUnit acquireTimeoutUnit;

  private volatile V controller;
  private volatile Disposable observation;

  private final Runnable statusUpdated;
  private final Consumer<V> controllerAcquired;
  private final Runnable controllerReleased;

  public DeviceDependency(
      Device<V> device,
      long time,
      TimeUnit unit,
      Consumer<DeviceDependency<V>> statusUpdated) {
    this.device = device;
    this.acquireTimeout = time;
    this.acquireTimeoutUnit = unit;

    this.statusUpdated = () -> statusUpdated.accept(this);
    this.controllerAcquired = lock -> {};
    this.controllerReleased = () -> {};
  }

  public DeviceDependency(
      Device<V> device,
      long time,
      TimeUnit unit,
      Consumer<DeviceDependency<V>> statusUpdated,
      Consumer<V> controllerAcquired,
      Runnable controllerReleased) {
    this.device = device;
    this.acquireTimeout = time;
    this.acquireTimeoutUnit = unit;

    this.statusUpdated = () -> statusUpdated.accept(this);
    this.controllerAcquired = controllerAcquired;
    this.controllerReleased = controllerReleased;
  }

  public synchronized void open() {
    if (observation == null) {
      this.observation = device
          .status()
          .value()
          .weakReference(this)
          .observe(status -> status.apply(DeviceDependency::updateStatus));
    }
  }

  public synchronized void close() {
    if (observation != null) {
      observation.cancel();
      observation = null;
      releaseController();
    }
  }

  private synchronized void updateStatus(DeviceStatus status) {
    try {
      switch (status) {
      case AVAILABLE:
        acquireController();
        break;
      case INACCESSIBLE:
        releaseController();
        break;
      case UNAVAILABLE:
        break;
      case DISPOSED:
        close();
        break;
      }
    } finally {
      statusUpdated();
    }
  }

  protected void statusUpdated() {
    statusUpdated.run();
  }

  protected void controllerAcquired(V lock) {
    controllerAcquired.accept(lock);
  }

  protected void controllerReleased() {
    controllerReleased.run();
  }

  public Device<V> getDevice() {
    return device;
  }

  public synchronized Optional<V> getController() {
    return Optional.ofNullable(controller);
  }

  private synchronized void acquireController() {
    if (controller == null) {
      try {
        controller = device.acquireControl(acquireTimeout, acquireTimeoutUnit);
      } catch (TimeoutException | InterruptedException e) {
        throw new IllegalStateException(e);
      }
      try {
        controllerAcquired(controller);
      } catch (Exception e) {
        try {
          releaseController();
        } catch (Exception ee) {
          e.addSuppressed(ee);
        }
        throw e;
      }
    }
  }

  private synchronized void releaseController() {
    if (controller != null) {
      try {
        controller.close();
      } catch (Exception e) {
        try {
          controller = null;
          controllerReleased();
        } catch (Exception ee) {
          e.addSuppressed(ee);
        }
        throw e;
      }
      controller = null;
      controllerReleased();
    }
  }
}