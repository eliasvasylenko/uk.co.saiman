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
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.virtual;

import static uk.co.saiman.instrument.DeviceStatus.AVAILABLE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import uk.co.saiman.instrument.Controller;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.DeviceStatus;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.observable.Disposable;

/**
 * An implementation of a virtual device which is an abstraction over any number
 * of other devices.
 * 
 * @author Elias N Vasylenko
 *
 */
public abstract class AbstractingDevice<U extends Controller> extends DeviceImpl<U> {
  private Map<Device<?>, DeviceDependency<?>> dependencies = new HashMap<>();

  public AbstractingDevice(String name, Instrument instrument) {
    super(name, instrument);
  }

  @Override
  protected void dispose() {
    synchronized (dependencies) {
      dependencies.values().stream().forEach(DeviceDependency::releaseController);
    }
    super.dispose();
  }

  private void checkDependencies() {
    synchronized (dependencies) {
      if (dependencies.values().stream().allMatch(DeviceDependency::isAvailable)) {
        setAccessible();
      } else {
        setInaccessible();
      }
    }
  }

  @Override
  protected U acquireControl(ControlLock lock) {
    synchronized (dependencies) {
      try {
        dependencies.values().stream().forEach(DeviceDependency::acquireController);
        checkDependencies();
      } catch (Exception e) {
        dependencies.values().stream().forEach(dependency -> {
          try {
            if (!dependency.isAutoAcquire()) {
              dependency.releaseController();
            }
          } catch (Exception e2) {
            e.addSuppressed(e2);
          }
        });
        throw e;
      }
    }

    return acquireControl(new AbstractingControlLock(lock));
  }

  protected abstract U acquireControl(AbstractingControlLock lock);

  @SuppressWarnings("unchecked")
  protected <V extends Controller> Optional<DeviceDependency<V>> getDependency(Device<V> device) {
    return Optional.ofNullable((DeviceDependency<V>) dependencies.get(device));
  }

  protected class DeviceDependency<V extends Controller> {
    private final Device<V> device;
    private final boolean autoAcquire;
    private Optional<? extends V> controller = Optional.empty();
    private Disposable observation;

    public DeviceDependency(Device<V> device, boolean autoAcquire) {
      this.device = device;
      this.autoAcquire = autoAcquire;

      this.observation = device
          .status()
          .value()
          .weakReference(this)
          .observe(status -> status.apply(DeviceDependency::updateStatus));

      synchronized (dependencies) {
        dependencies.put(device, this);
      }
    }

    private void updateStatus(DeviceStatus status) {
      try {
        switch (status) {
        case AVAILABLE:
          if (autoAcquire) {
            acquireController();
          }
          break;
        case INACCESSIBLE:
          releaseController();
          break;
        case UNAVAILABLE:
          if (controller.isEmpty()) {
            releaseController();
          }
          break;
        case DISPOSED:
          dispose();
          break;
        }
      } finally {
        checkDependencies();
      }
    }

    public boolean isAutoAcquire() {
      return autoAcquire;
    }

    private synchronized void acquireController() {
      if (controller.isEmpty()) {
        controller = device.acquireControl();
        controller.ifPresent(c -> {
          try {
            controllerAcquired(c);
          } catch (Exception e) {
            releaseControl();
            throw e;
          }
        });
      }
    }

    protected void controllerAcquired(V controller) {}

    private synchronized boolean isAvailable() {
      return controller.isPresent() || device.status().isEqual(AVAILABLE);
    }

    private synchronized void releaseController() {
      try {
        controller.ifPresent(Controller::close);
      } catch (Exception e) {
        try {
          controller = Optional.empty();
          controllerReleased();
        } catch (Exception e2) {
          e.addSuppressed(e2);
        }
        throw e;
      }
      controller = Optional.empty();
      controllerReleased();
    }

    protected void controllerReleased() {}

    public void dispose() {
      observation.cancel();
    }
  }

  public class AbstractingControlLock {
    private final ControlLock component;

    public AbstractingControlLock(ControlLock component) {
      this.component = component;
    }

    public boolean isOpen() {
      return component.isOpen();
    }

    public <V extends Controller> V getController(Device<V> device) {
      return get(
          () -> getDependency(device)
              .flatMap(d -> d.controller)
              .orElseThrow(IllegalStateException::new));
    }

    public <V extends Controller> V getController(DeviceDependency<V> dependency) {
      return get(() -> dependency.controller.orElseThrow(IllegalStateException::new));
    }

    public void run(Runnable runnable) {
      component.run(runnable);
    }

    public <T> T get(Supplier<T> supplier) {
      return component.get(supplier);
    }

    public synchronized void close() {
      component.close();
    }

    public synchronized void close(Runnable runnable) {
      component.close(runnable);
    }
  }
}
