package uk.co.saiman.maldi.stage.msapex;

import javax.inject.Inject;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.maldi.stage.MaldiStage;

public class MaldiImagingPlatePart {
  private final MaldiStageDiagram diagram;

  @Inject
  public MaldiImagingPlatePart(
      BorderPane container,
      MaldiStage stage,
      SamplePlatePresenter presenter) {
    diagram = new MaldiStageDiagram(
        stage,
        new Image(getClass().getClassLoader().getResourceAsStream("/slides/imaging.jpg")));
    container.setCenter(diagram);
  }
}
