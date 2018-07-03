package uk.co.saiman.acquisition.adq.impl;

import uk.co.saiman.acquisition.adq.AdqDevice;

public abstract class AdqDeviceImpl implements AdqDevice {
  private final AdqDeviceManager manager;

  public AdqDeviceImpl(AdqDeviceManager manager) {
    this.manager = manager;
  }
}
