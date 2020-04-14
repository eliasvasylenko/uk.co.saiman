package uk.co.saiman.instrument.stage.msapex;

import java.util.Optional;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class ActualXYPositionAnnotation extends XYPositionAnnotation {
  public ActualXYPositionAnnotation(Stage<XYCoordinate<Length>> stage) {
    stage
        .samplePosition()
        .optionalValue()
        .weakReference(this)
        .observe(
            o -> o.message().ifPresentOrElse(o.owner()::setPosition, o.owner()::unsetPosition));
  }

  protected void setPosition(Optional<XYCoordinate<Length>> actualPosition) {
    actualPosition.ifPresentOrElse(position -> {
      setActualPosition(position);
      setVisible(true);
    }, this::unsetPosition);
  }

  protected void unsetPosition() {
    setVisible(false);
  }

  protected void setActualPosition(XYCoordinate<Length> position) {
    setMeasurementX(position.getX());
    setMeasurementY(position.getY());
  }
}
