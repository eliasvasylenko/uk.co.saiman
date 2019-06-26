package uk.co.saiman.instrument;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.saiman.instrument.DeviceStatus.AVAILABLE;
import static uk.co.saiman.instrument.DeviceStatus.DISPOSED;
import static uk.co.saiman.instrument.DeviceStatus.INACCESSIBLE;
import static uk.co.saiman.instrument.DeviceStatus.UNAVAILABLE;

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

  static interface SimpleController {
    void command();
  }

  class SimpleDevice extends DeviceImpl<SimpleController> {
    public SimpleDevice() {
      super("simpleDevice", instrument);
      setAccessible();
    }

    @Override
    protected SimpleController createController(ControlContext context) {
      backingService.acquireControl();
      return new SimpleController() {
        @Override
        public void command() {
          context.run(backingService::command);
        }
      };
    }

    @Override
    protected void destroyController(ControlContext context) {
      backingService.releaseControl();
    }
  }

  @Mock
  Instrument instrument;
  @Mock
  DeviceRegistration deviceRegistration;
  @Mock
  InstrumentRegistration instrumentRegistration;
  @Mock
  BackingService backingService;

  @Test
  public void createDeviceSucceeds() {
    new SimpleDevice();
  }

  @Test
  public void getInstrumentRegistration() throws Exception {
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());
    Mockito.doReturn(instrumentRegistration).when(deviceRegistration).getInstrumentRegistration();

    var device = new SimpleDevice();

    assertEquals(instrumentRegistration, device.getInstrumentRegistration());
  }

  @Test
  public void acquireFailsWhenAlreadyAcquired() throws TimeoutException, InterruptedException {
    var device = new SimpleDevice();
    assertFalse(device.acquireControl(0, SECONDS).isClosed());
    assertThrows(TimeoutException.class, () -> device.acquireControl(0, SECONDS));
  }

  @Test
  public void acquireSucceedsAfterAcquireAndRelease() throws Exception {
    var device = new SimpleDevice();
    var control = device.acquireControl(0, SECONDS);
    control.close();
    assertFalse(device.acquireControl(0, SECONDS).isClosed());
  }

  @Test
  public void getNameTest() throws Exception {
    var device = new SimpleDevice();
    assertEquals("simpleDevice", device.getName());
  }

  @Test
  public void disposeDeregistersFromInstrument() throws Exception {
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());

    var device = new SimpleDevice();
    device.dispose();
    assertEquals(DISPOSED, device.status().get());

    var inOrder = Mockito.inOrder(instrument, deviceRegistration);
    inOrder.verify(instrument).registerDevice(device);
    inOrder.verify(deviceRegistration).deregister();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void acquireControlFailsAfterDispose() throws Exception {
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());

    var device = new SimpleDevice();
    device.dispose();
    assertEquals(DISPOSED, device.status().get());
    assertThrows(IllegalStateException.class, () -> device.acquireControl(0, SECONDS));
    assertEquals(DISPOSED, device.status().get());
  }

  @Test
  public void successfulAcquireControlWhenInaccessibleMakesUnavailable() throws Exception {
    Mockito.when(instrument.registerDevice(Mockito.any())).thenReturn(deviceRegistration);

    var device = new SimpleDevice();
    device.setInaccessible();
    assertEquals(INACCESSIBLE, device.status().get());
    assertFalse(device.acquireControl(0, SECONDS).isClosed());
    assertEquals(UNAVAILABLE, device.status().get());
  }

  @Test
  public void unsuccessfulAcquireControlMakesInaccessible() throws Exception {
    RuntimeException exception = new RuntimeException();
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());
    Mockito.doThrow(exception).when(backingService).acquireControl();

    var device = new SimpleDevice();
    assertEquals(AVAILABLE, device.status().get());
    assertEquals(
        exception,
        assertThrows(RuntimeException.class, () -> device.acquireControl(0, SECONDS)));
    assertEquals(INACCESSIBLE, device.status().get());
  }

  @Test
  public void setInaccessibleWhenDisposedDoesNothing() throws Exception {
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());

    var device = new SimpleDevice();
    device.dispose();
    device.setInaccessible();
    assertEquals(DISPOSED, device.status().get());
  }

  @Test
  public void setAccessibleWhenDisposedDoesNothing() throws Exception {
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());

    var device = new SimpleDevice();
    device.dispose();
    device.setAccessible();
    assertEquals(DISPOSED, device.status().get());
  }

  @Test
  public void setInaccessibleWhenAcquiredControlReleasesControl() throws Exception {
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());

    var device = new SimpleDevice();
    var control = device.acquireControl(0, SECONDS).getController();
    device.setInaccessible();
    assertThrows(IllegalStateException.class, () -> control.command());

    var inOrder = Mockito.inOrder(instrument, backingService, deviceRegistration);
    inOrder.verify(instrument).registerDevice(device);
    inOrder.verify(backingService).acquireControl();
    inOrder.verify(backingService).releaseControl();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void disposeWhenAcquiredControlReleasesControl() throws Exception {
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());

    var device = new SimpleDevice();
    var control = device.acquireControl(0, SECONDS).getController();
    device.dispose();
    assertThrows(IllegalStateException.class, () -> control.command());

    var inOrder = Mockito.inOrder(instrument, backingService, deviceRegistration);
    inOrder.verify(instrument).registerDevice(device);
    inOrder.verify(backingService).acquireControl();
    inOrder.verify(backingService).releaseControl();
    inOrder.verify(deviceRegistration).deregister();
    inOrder.verifyNoMoreInteractions();
  }
}
