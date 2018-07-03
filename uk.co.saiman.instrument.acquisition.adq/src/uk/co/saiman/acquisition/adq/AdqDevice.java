package uk.co.saiman.acquisition.adq;

import uk.co.saiman.acquisition.AcquisitionDevice;

public interface AdqDevice extends AcquisitionDevice {
  AdqProductId getProductId();

  AdqHardwareInterface getHardwareInterface();
}
