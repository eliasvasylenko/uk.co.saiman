package uk.co.saiman.maldi.stage.msapex;

import javax.inject.Inject;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.maldi.stage.MaldiStage;

public class Maldi96WellCalibrantPlatePart {
  private final MaldiStageDiagram diagram;

  @Inject
  public Maldi96WellCalibrantPlatePart(
      BorderPane container,
      MaldiStage stage,
      SamplePlatePresenter presenter) {
    diagram = new MaldiStageDiagram(
        stage,
        new Image(getClass().getClassLoader().getResourceAsStream("/slides/96-well-with-cal.jpg")));
    container.setCenter(diagram);
  }
}
