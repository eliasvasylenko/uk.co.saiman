package uk.co.saiman.maldi.stage.msapex;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.msapex.environment.ResourcePresenter;
import uk.co.saiman.maldi.stage.MaldiStageConstants;
import uk.co.saiman.maldi.stage.SamplePlateStage;
import uk.co.saiman.maldi.stage.i18n.MaldiStageProperties;

@Component(enabled = true, immediate = true)
public class MaldiSamplePlateStagePresenter implements ResourcePresenter<SamplePlateStage> {
  private final MaldiStageProperties properties;

  @Activate
  public MaldiSamplePlateStagePresenter(@Reference MaldiStageProperties properties) {
    this.properties = properties;
  }

  @Override
  public String getLocalizedLabel() {
    return properties.samplePlateExperimentStepName().get();
  }

  @Override
  public String getIconURI() {
    return "fugue:size16/flask.png";
  }

  @Override
  public Class<? super SamplePlateStage> getResourceClass() {
    return SamplePlateStage.class;
  }

  @Override
  public Provision<SamplePlateStage> getProvision() {
    return MaldiStageConstants.MALDI_SAMPLE_PLATE_DEVICE;
  }
}
