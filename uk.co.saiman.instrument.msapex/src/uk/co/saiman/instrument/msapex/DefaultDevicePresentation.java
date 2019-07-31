package uk.co.saiman.instrument.msapex;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.msapex.device.DevicePresentation;

public class DefaultDevicePresentation implements DevicePresentation {
  private final Device<?> device;

  public DefaultDevicePresentation(Device<?> device) {
    this.device = device;
  }

  @Override
  public String getLabel() {
    return device.toString();
  }

  @Override
  public String getLocalizedLabel() {
    return device.toString();
  }

  @Override
  public String getIconURI() {
    return null;
  }

  @Override
  public boolean presentsDevice(Device<?> device) {
    return this.device.equals(device);
  }
}
