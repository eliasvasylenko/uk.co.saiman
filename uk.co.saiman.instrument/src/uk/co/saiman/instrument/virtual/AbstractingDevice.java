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

import static uk.co.saiman.instrument.DeviceStatus.AVAILABLE;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import uk.co.saiman.instrument.Controller;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceImpl;

/**
 * An implementation of a virtual device which is an abstraction over any number
 * of other devices.
 * 
 * @author Elias N Vasylenko
 *
 */
public abstract class AbstractingDevice<U extends Controller> extends DeviceImpl<U> {
  private final Map<Device<?>, DeviceDependency<?>> dependencies = new HashMap<>();

  public AbstractingDevice(String name) {
    super(name);
  }

  @Override
  protected synchronized void dispose() {
    dependencies.values().stream().forEach(DeviceDependency::close);
    super.dispose();
  }

  synchronized void checkDependencies() {
    if (dependencies
        .values()
        .stream()
        .allMatch(
            d -> d.getController().isPresent() || d.getDevice().status().isEqual(AVAILABLE))) {
      setAccessible();
    } else {
      setInaccessible();
    }
  }

  @Override
  protected U createController(ControlContext context) {
    Map<Device<?>, Controller> dependencyControls = new HashMap<>();

    synchronized (dependencies) {
      try {
        for (var dependency : dependencies.values()) {
          dependency.open();
          var control = dependency.getController();
          if (control.isEmpty() || control.get().isClosed()) {
            throw new IllegalStateException();
          }
          dependencyControls.put(dependency.getDevice(), control.get());
        }
      } catch (Exception e) {
        for (var dependency : dependencies.values()) {
          try {
            dependency.close();
          } catch (Exception ee) {
            e.addSuppressed(ee);
          }
        }
        throw e;
      }
    }

    return createController(new DependentControlContext() {
      @Override
      public void close() {
        context.close();
      }

      @Override
      public boolean isClosed() {
        return context.isClosed();
      }

      @Override
      public void run(Runnable runnable) {
        context.run(runnable);
      }

      @Override
      public <T> T get(Supplier<T> supplier) {
        return context.get(supplier);
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends Controller> T getController(Device<T> device) {
        return (T) dependencyControls.get(device);
      }
    });
  }

  @Override
  protected void destroyController(ControlContext context) {
    // TODO check all our device dependencies are still acquired
  }

  protected abstract U createController(DependentControlContext context);

  protected void addDependency(Device<?> device, long time, TimeUnit unit) {
    DeviceDependency<?> dependency = new DeviceDependency<>(
        device,
        time,
        unit,
        dep -> checkDependencies());
    dependencies.put(device, dependency);
  }

  public interface DependentControlContext extends ControlContext {
    <T extends Controller> T getController(Device<T> device);
  }
}
