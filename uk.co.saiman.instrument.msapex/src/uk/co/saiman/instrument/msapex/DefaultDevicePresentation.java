package uk.co.saiman.instrument.msapex;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.msapex.device.DevicePresenter;

public class DefaultDevicePresentation implements DevicePresenter {
  private final Device<?> device;

  public DefaultDevicePresentation(Device<?> device) {
    this.device = device;
  }

  @Override
  public String getLocalizedLabel() {
    return device.toString();
  }

  @Override
  public String getIconURI() {
    return "fugue:size16/plug.png";
  }

  @Override
  public boolean presentsDevice(Device<?> device) {
    return device == this.device;
  }
}
