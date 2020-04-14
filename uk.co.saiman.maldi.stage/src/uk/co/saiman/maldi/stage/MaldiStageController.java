package uk.co.saiman.maldi.stage;

import uk.co.saiman.instrument.DeviceImpl.ControlContext;
import uk.co.saiman.instrument.stage.sampleplate.SamplePlateStageController;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePreparation;

public class MaldiStageController extends SamplePlateStageController<MaldiSamplePreparation> {
  public MaldiStageController(MaldiStage stage, ControlContext context) {
    super(stage, context);
  }
}