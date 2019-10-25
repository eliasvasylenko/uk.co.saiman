package uk.co.saiman.instrument.stage.msapex;

import javax.measure.quantity.Length;

import javafx.scene.shape.Line;
import uk.co.saiman.msapex.annotations.XYAnnotation;

public class StagePositionAnnotation extends XYAnnotation<Length, Length> {
  public StagePositionAnnotation() {
    Line downLine = new Line(-5, -5, 5, 5);
    Line upLine = new Line(-5, 5, 5, -5);
    downLine.setStrokeWidth(1);
    upLine.setStrokeWidth(1);
    getChildren().add(downLine);
    getChildren().add(upLine);
  }
}
