package uk.co.saiman.maldi.stage.msapex;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import javafx.scene.layout.BorderPane;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.variables.Variables;

public class MaldiStagePart {
  @Inject
  private BorderPane container;

  @Inject
  void setSamplePlate(@Optional SamplePlatePresenter presenter) {
    if (presenter != null) {
      container.setCenter(presenter.getWidget());
    } else {
      container.setCenter(null);
    }
  }
  
  @Inject
  void setStep(@Optional Step step, @Optional Variables variables) {
    if (step != null) {
      
    }
  }
}
