package uk.co.saiman.simulation.instrument.impl;

import java.util.concurrent.TimeUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import uk.co.saiman.acquisition.AcquisitionControl;
import uk.co.saiman.instrument.DeviceControlImpl;

public class SimulatedAcquisitionControl extends DeviceControlImpl<SimulatedAcquisitionDevice>
    implements AcquisitionControl {
  public SimulatedAcquisitionControl(
      SimulatedAcquisitionDevice device,
      long timeout,
      TimeUnit unit) {
    super(device, timeout, unit);
  }

  @Override
  public void startAcquisition() {
    getDevice().startAcquisition();
  }

  @Override
  public void setAcquisitionCount(int count) {
    getDevice().setAcquisitionCount(count);
  }

  @Override
  public void setAcquisitionTime(Quantity<Time> time) {
    getDevice().setAcquisitionTime(time);
  }

  @Override
  public void setSampleDepth(int depth) {
    getDevice().setSampleDepth(depth);
  }
}
