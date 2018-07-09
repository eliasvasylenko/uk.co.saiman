package uk.co.saiman.instrument;

/**
 * A registration of a device with an instrument. A device registration is
 * created when a device is {@link Instrument#registerDevice(Device) registered}
 * to an instrument. It should be owned by the instrument, and may be obtained
 * via {@link Instrument#getRegistrations()} or
 * {@link Device#getRegistration()}.
 * <p>
 * Each instance is mirrored by a corresponding {@link InstrumentRegistration},
 * which is owned by the device.
 * <p>
 * An instance of {@link InstrumentRegistration} cannot be obtained from an
 * instrument of {@link DeviceRegistration}. This is to ensure that device
 * registrations can be passed safely around by users of the instrument API,
 * while the power to unregister a device can remain properly encapsulated by
 * the caller and implementor of {@link Instrument#registerDevice(Device)}.
 * 
 * @author Elias N Vasylenko
 */
public interface DeviceRegistration {
  boolean isRegistered();

  Instrument getInstrument();

  Device getDevice();
}
