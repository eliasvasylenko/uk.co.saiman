package uk.co.saiman.instrument.stage.msapex;

import java.util.Optional;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.sample.Analysis;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class RequestedXYPositionAnnotation extends XYPositionAnnotation {
  public RequestedXYPositionAnnotation(Stage<XYCoordinate<Length>> stage) {
    stage
        .requestedSampleState()
        .optionalValue()
        .weakReference(this)
        .observe(
            o -> o
                .message()
                .ifPresentOrElse(o.owner()::setRequestedState, o.owner()::unsetRequestedState));
  }

  protected void setRequestedState(
      Optional<RequestedSampleState<XYCoordinate<Length>>> requestedState) {
    requestedState
        .filter(r -> r instanceof Analysis<?>)
        .map(r -> (Analysis<XYCoordinate<Length>>) r)
        .map(Analysis::position)
        .ifPresentOrElse(position -> {
          setRequestedPosition(position);
          setVisible(true);
        }, () -> unsetRequestedState());
  }

  protected void unsetRequestedState() {
    setVisible(false);
  }

  protected void setRequestedPosition(XYCoordinate<Length> position) {
    setMeasurementX(position.getX());
    setMeasurementY(position.getY());
  }
}
