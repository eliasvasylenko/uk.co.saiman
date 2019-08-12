package uk.co.saiman.simulation.msapex;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.msapex.device.DevicePresenter;
import uk.co.saiman.simulation.instrument.impl.SimulatedAcquisitionDevice;

@ServiceRanking(10)
@Component
public class SimulatedAcquisitionDevicePresenter implements DevicePresenter {
  public SimulatedAcquisitionDevicePresenter() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public String getLocalizedLabel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getIconURI() {
    return null;
  }

  @Override
  public boolean presentsDevice(Device<?> device) {
    return device instanceof SimulatedAcquisitionDevice;
  }
}
