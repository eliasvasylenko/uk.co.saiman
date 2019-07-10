package uk.co.saiman.maldi.acquisition;

import uk.co.saiman.experiment.environment.Provision;
import uk.co.saiman.instrument.acquisition.AcquisitionController;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;

public final class MaldiAcquisitionConstants {
  private MaldiAcquisitionConstants() {}

  public static final Provision<AcquisitionDevice<?>> MALDI_ACQUISITION_DEVICE = new Provision<>(
      "uk.co.saiman.maldi.acquisition.device.provision");
  public static final Provision<AcquisitionController> MALDI_ACQUISITION_CONTROLLER = new Provision<>(
      "uk.co.saiman.maldi.acquisition.controller.provision");
}
