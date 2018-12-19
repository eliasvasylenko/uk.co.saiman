package uk.co.saiman.instrument.stage.composed;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.stage.XYStageControl;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class ComposedXYStageControl extends ComposedStageControl<XYCoordinate<Length>>
    implements XYStageControl {
  public ComposedXYStageControl(ComposedXYStage<?> device, long timeout, TimeUnit unit) {
    super(device, timeout, unit);
  }
}
