package uk.co.saiman.instrument.acquisition.msapex;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.instrument.acquisition.msapex.i18n.AcquisitionProperties;
import uk.co.saiman.instrument.msapex.device.DevicePresenter;

@ServiceRanking(10)
@Component
public class AcquisitionDevicePresenter implements DevicePresenter {
  private final AcquisitionProperties properties;

  @Activate
  public AcquisitionDevicePresenter(@Reference AcquisitionProperties properties) {
    this.properties = properties;
  }

  @Override
  public String getLocalizedLabel() {
    return properties.acquisitionDevice().get();
  }

  @Override
  public String getIconURI() {
    return "fugue:size16/system-monitor.png";
  }

  @Override
  public boolean presentsDevice(Device<?> device) {
    return device instanceof AcquisitionDevice;
  }
}
