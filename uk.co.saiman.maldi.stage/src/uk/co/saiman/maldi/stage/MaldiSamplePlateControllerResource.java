package uk.co.saiman.maldi.stage;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.osgi.CloseableResourceProvider;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;

@Component(service = ExclusiveResourceProvider.class)
public class MaldiSamplePlateControllerResource
    implements CloseableResourceProvider<SamplePlateStageController> {

  @Override
  public Provision<SamplePlateStageController> getProvision() {
    return MaldiStageConstants.MALDI_SAMPLE_PLATE_CONTROLLER;
  }

  @Override
  public SamplePlateStageController deriveValue(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception {
    return globalEnvironment
        .provideValue(MaldiStageConstants.MALDI_SAMPLE_PLATE_DEVICE)
        .acquireControl(timeout, unit);
  }
}
