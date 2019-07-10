package uk.co.saiman.maldi.stage.msapex;

import javax.annotation.PostConstruct;

import javafx.scene.layout.BorderPane;
import uk.co.saiman.instrument.msapex.device.DevicePresentationService;
import uk.co.saiman.maldi.stage.SampleAreaStage;
import uk.co.saiman.maldi.stage.SamplePlateStage;

public class MaldiStagePart {
  @PostConstruct
  void initialize(
      BorderPane container,
      SamplePlateStage samplePlateStage,
      SampleAreaStage sampleAreaStage,
      DevicePresentationService devicePresentation) {}
}
