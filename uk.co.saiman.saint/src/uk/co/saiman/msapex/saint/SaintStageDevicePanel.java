package uk.co.saiman.msapex.saint;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.msapex.instrument.sample.SampleDevicePanel;
import uk.co.saiman.saint.stage.impl.SaintSamplePlateExecutor;

@Component
public class SaintStageDevicePanel implements SampleDevicePanel {
  private final SaintSamplePlateExecutor stageExecutor;

  @Activate
  public SaintStageDevicePanel(
      @Reference(name = "stageExecutor") SaintSamplePlateExecutor stageExecutor) {
    this.stageExecutor = stageExecutor;
  }

  @Override
  public SampleDevice<?, ?> device() {
    return stageExecutor.sampleDevice();
  }

  @Override
  public Class<?> paneModelClass() {
    return SaintStageDeviceModel.class;
  }
}
