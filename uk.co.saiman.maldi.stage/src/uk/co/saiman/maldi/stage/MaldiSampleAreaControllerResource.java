package uk.co.saiman.maldi.stage;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.osgi.CloseableResourceProvider;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;

@Component(service = ExclusiveResourceProvider.class)
public class MaldiSampleAreaControllerResource
    implements CloseableResourceProvider<SampleAreaStageController> {

  @Override
  public Class<SampleAreaStageController> getProvision() {
    return SampleAreaStageController.class;
  }

  @Override
  public SampleAreaStageController deriveValue(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception {
    return globalEnvironment.provideValue(SampleAreaStage.class).acquireControl(timeout, unit);
  }
}
