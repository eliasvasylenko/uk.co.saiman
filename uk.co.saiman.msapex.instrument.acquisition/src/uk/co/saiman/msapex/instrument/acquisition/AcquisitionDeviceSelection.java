package uk.co.saiman.msapex.instrument.acquisition;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.acquisition.AcquisitionDevice;

public class AcquisitionDeviceSelection {
  private List<AcquisitionDevice> devices;

  public AcquisitionDeviceSelection() {
    this.devices = emptyList();
  }

  public AcquisitionDeviceSelection(Collection<? extends AcquisitionDevice> devices) {
    this.devices = new ArrayList<>(devices);
  }

  public Stream<AcquisitionDevice> getSelectedDevices() {
    return devices.stream();
  }
}
