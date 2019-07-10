package uk.co.saiman.maldi.acquisition.msapex;

import javax.annotation.PostConstruct;

import javafx.scene.layout.BorderPane;
import uk.co.saiman.experiment.environment.StaticEnvironment;
import uk.co.saiman.instrument.acquisition.msapex.AcquisitionChart;
import uk.co.saiman.instrument.msapex.device.DevicePresentationService;
import uk.co.saiman.maldi.acquisition.MaldiAcquisitionConstants;

public class MaldiAcquisitionPart {
  static final String OSGI_SERVICE = "osgi.service";

  @PostConstruct
  void initialize(
      BorderPane container,
      StaticEnvironment environment,
      DevicePresentationService devicePresentation) {
    var chart = new AcquisitionChart(
        environment.getStaticValue(MaldiAcquisitionConstants.MALDI_ACQUISITION_DEVICE),
        devicePresentation);
    container.getChildren().add(chart);
  }
}
