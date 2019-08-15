package uk.co.saiman.maldi.stage;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.osgi.CloseableResourceProvider;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;

@Component(service = ExclusiveResourceProvider.class)
public class MaldiSamplePlateControllerResource
    implements CloseableResourceProvider<SamplePlateStageController> {

  @Override
  public Class<SamplePlateStageController> getProvision() {
    return SamplePlateStageController.class;
  }

  @Override
  public SamplePlateStageController deriveValue(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception {
    return globalEnvironment.provideValue(SamplePlateStage.class).acquireControl(timeout, unit);
  }
}
