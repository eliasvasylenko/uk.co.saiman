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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.saiman.instrument.ControllerStatus.AVAILABLE;
import static uk.co.saiman.instrument.ControllerStatus.UNAVAILABLE;
import static uk.co.saiman.instrument.DeviceStatus.ACCESSIBLE;
import static uk.co.saiman.instrument.DeviceStatus.DISPOSED;
import static uk.co.saiman.instrument.DeviceStatus.INACCESSIBLE;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeviceImplTest {
  static interface BackingService {
    void acquireControl();

    void command();

    void releaseControl();
  }

  static interface SimpleController extends Controller {
    void command();
  }

  class SimpleDevice extends DeviceImpl<SimpleController> {
    public SimpleDevice() {
      setAccessible();
    }

    @Override
    protected SimpleController createController(ControlContext context) {
      backingService.acquireControl();
      return new SimpleController() {
        @Override
        public void command() {
          try (var lock = context.acquireLock()) {
            backingService.command();
          }
          setAccessible(); // it worked
        }

        @Override
        public void close() {
          context.close();
        }

        @Override
        public boolean isOpen() {
          return context.isOpen();
        }
      };
    }

    @Override
    protected void destroyController(ControlContext context) {
      backingService.releaseControl();
    }
  }

  @Mock
  BackingService backingService;

  @Test
  public void createDeviceSucceeds() {
    new SimpleDevice();
  }

  @Test
  public void acquireFailsWhenAlreadyAcquired() throws TimeoutException, InterruptedException {
    var device = new SimpleDevice();
    assertTrue(device.acquireControl(0, SECONDS).isOpen());
    assertThrows(TimeoutException.class, () -> device.acquireControl(0, SECONDS));
  }

  @Test
  public void acquireSucceedsAfterAcquireAndRelease() throws Exception {
    var device = new SimpleDevice();
    var control = device.acquireControl(0, SECONDS);
    control.close();
    assertTrue(device.acquireControl(0, SECONDS).isOpen());
  }

  @Test
  public void acquireControlFailsAfterDispose() throws Exception {
    var device = new SimpleDevice();
    device.setDisposed();
    assertEquals(DISPOSED, device.status().get());
    assertThrows(IllegalStateException.class, () -> device.acquireControl(0, SECONDS));
    assertEquals(DISPOSED, device.status().get());
  }

  @Test
  public void successfulAcquireControlWhenInaccessibleMakesUnavailable() throws Exception {
    var device = new SimpleDevice();
    device.setInaccessible();
    assertEquals(INACCESSIBLE, device.status().get());
    assertEquals(AVAILABLE, device.controllerStatus().get());
    assertTrue(device.acquireControl(0, SECONDS).isOpen());
    assertEquals(ACCESSIBLE, device.status().get());
    assertEquals(UNAVAILABLE, device.controllerStatus().get());
  }

  @Test
  public void unsuccessfulAcquireControlMakesInaccessible() throws Exception {
    RuntimeException exception = new RuntimeException();
    Mockito.doThrow(exception).when(backingService).acquireControl();

    var device = new SimpleDevice();
    assertEquals(ACCESSIBLE, device.status().get());
    assertEquals(AVAILABLE, device.controllerStatus().get());
    assertEquals(
        exception,
        assertThrows(RuntimeException.class, () -> device.acquireControl(0, SECONDS)));
    assertEquals(INACCESSIBLE, device.status().get());
    assertEquals(AVAILABLE, device.controllerStatus().get());
  }

  @Test
  public void setInaccessibleWhenDisposedDoesNothing() throws Exception {
    var device = new SimpleDevice();
    device.setDisposed();
    device.setInaccessible();
    assertEquals(DISPOSED, device.status().get());
  }

  @Test
  public void setAccessibleWhenDisposedDoesNothing() throws Exception {
    var device = new SimpleDevice();
    device.setDisposed();
    device.setAccessible();
    assertEquals(DISPOSED, device.status().get());
  }

  @Test
  public void successfulAcquireLockWhenInaccessibleMakesUnavailable() throws Exception {
    var device = new SimpleDevice();
    var control = device.acquireControl(0, SECONDS);
    device.setInaccessible();
    control.command();

    assertEquals(ACCESSIBLE, device.status().get());
    assertEquals(UNAVAILABLE, device.controllerStatus().get());

    var inOrder = Mockito.inOrder(backingService);
    inOrder.verify(backingService).acquireControl();
    inOrder.verify(backingService).command();
    inOrder.verifyNoMoreInteractions();
  }
}
