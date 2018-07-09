package uk.co.saiman.instrument;

/**
 * A registration with an instrument. An instrument registration is obtained
 * when a device {@link Instrument#registerDevice(Device) registers} itself to
 * an instrument. It should be owned by the registered device, and typically
 * should not be exposed outside the implementation of the device.
 * <p>
 * Each instance is mirrored by a corresponding {@link DeviceRegistration},
 * which is owned by the instrument.
 * 
 * @author Elias N Vasylenko
 *
 */
public interface InstrumentRegistration {
  void unregister();

  /**
   * @return the corresponding device registration
   */
  DeviceRegistration getDeviceRegistration();
}
