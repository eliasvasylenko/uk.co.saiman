package uk.co.saiman.maldi.acquisition;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.osgi.CloseableResourceProvider;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;
import uk.co.saiman.instrument.acquisition.AcquisitionController;

@Component(service = ExclusiveResourceProvider.class)
public class MaldiAcquisitionControllerResource
    implements CloseableResourceProvider<AcquisitionController> {

  @Override
  public Provision<AcquisitionController> getProvision() {
    return MaldiAcquisitionConstants.MALDI_ACQUISITION_CONTROLLER;
  }

  @Override
  public AcquisitionController deriveValue(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception {
    return globalEnvironment
        .provideValue(MaldiAcquisitionConstants.MALDI_ACQUISITION_DEVICE)
        .acquireControl(timeout, unit);
  }
}
