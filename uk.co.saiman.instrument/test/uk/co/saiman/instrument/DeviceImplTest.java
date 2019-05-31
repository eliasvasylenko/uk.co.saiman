package uk.co.saiman.instrument;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.saiman.instrument.DeviceStatus.AVAILABLE;
import static uk.co.saiman.instrument.DeviceStatus.DISPOSED;
import static uk.co.saiman.instrument.DeviceStatus.INACCESSIBLE;
import static uk.co.saiman.instrument.DeviceStatus.UNAVAILABLE;

import org.junit.jupiter.api.Assertions;
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
      super("simpleDevice", instrument);
      setAccessible();
    }

    @Override
    protected SimpleController acquireControl(ControlLock lock) {
      backingService.acquireControl();
      return new SimpleController() {
        @Override
        public void close() {
          lock.close(backingService::releaseControl);
        }

        @Override
        public void command() {
          lock.run(backingService::command);
        }
      };
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
  public void acquireFailsWhenAlreadyAcquired() {
    var device = new SimpleDevice();
    Assertions.assertTrue(device.acquireControl().isPresent());
    Assertions.assertTrue(device.acquireControl().isEmpty());
  }

  @Test
  public void acquireSucceedsAfterAcquireAndRelease() throws Exception {
    var device = new SimpleDevice();
    var control = device.acquireControl().get();
    control.close();
    Assertions.assertTrue(device.acquireControl().isPresent());
  }

  @Test
  public void getNameTest() throws Exception {
    var device = new SimpleDevice();
    Assertions.assertEquals("simpleDevice", device.getName());
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
    assertTrue(device.acquireControl().isEmpty());
    assertThrows(IllegalStateException.class, () -> device.acquireControl(0, SECONDS));
    assertEquals(DISPOSED, device.status().get());
  }

  @Test
  public void successfulAcquireControlWhenInaccessibleMakesUnavailable() throws Exception {
    Mockito.when(instrument.registerDevice(Mockito.any())).thenReturn(deviceRegistration);

    var device = new SimpleDevice();
    device.setInaccessible();
    assertEquals(INACCESSIBLE, device.status().get());
    assertTrue(device.acquireControl().isPresent());
    assertEquals(UNAVAILABLE, device.status().get());
  }

  @Test
  public void unsuccessfulAcquireControlMakesInaccessible() throws Exception {
    RuntimeException exception = new RuntimeException();
    Mockito.doReturn(deviceRegistration).when(instrument).registerDevice(Mockito.any());
    Mockito.doThrow(exception).when(backingService).acquireControl();

    var device = new SimpleDevice();
    assertEquals(AVAILABLE, device.status().get());
    assertEquals(exception, assertThrows(RuntimeException.class, () -> device.acquireControl()));
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
    var control = device.acquireControl().get();
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
    var control = device.acquireControl().get();
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
