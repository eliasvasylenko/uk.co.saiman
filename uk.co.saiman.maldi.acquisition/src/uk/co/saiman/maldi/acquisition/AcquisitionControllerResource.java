package uk.co.saiman.maldi.acquisition;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.osgi.CloseableResourceProvider;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;
import uk.co.saiman.instrument.acquisition.AcquisitionController;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;

@Component(service = ExclusiveResourceProvider.class)
public class AcquisitionControllerResource
    implements CloseableResourceProvider<AcquisitionController> {

  @Override
  public Class<AcquisitionController> getProvision() {
    return AcquisitionController.class;
  }

  @Override
  public AcquisitionController deriveValue(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception {
    return globalEnvironment.provideValue(AcquisitionDevice.class).acquireControl(timeout, unit);
  }
}
