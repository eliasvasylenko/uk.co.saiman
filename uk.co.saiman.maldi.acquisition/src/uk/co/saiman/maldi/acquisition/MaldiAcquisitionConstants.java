package uk.co.saiman.maldi.acquisition;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.instrument.acquisition.AcquisitionController;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;

public final class MaldiAcquisitionConstants {
  private MaldiAcquisitionConstants() {}

  public static final String MALDI_ACQUISITION_DEVICE_ID = "uk.co.saiman.maldi.acquisition.device.provision";
  public static final Provision<AcquisitionDevice<?>> MALDI_ACQUISITION_DEVICE = new Provision<>(
      MALDI_ACQUISITION_DEVICE_ID);

  public static final Provision<AcquisitionController> MALDI_ACQUISITION_CONTROLLER = new Provision<>(
      "uk.co.saiman.maldi.acquisition.controller.provision");
}
