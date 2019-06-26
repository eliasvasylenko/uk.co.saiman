package uk.co.saiman.saint.stage;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.observable.ObservableValue;

public interface SamplePlateStage extends Stage<SampleArea, SamplePlateStageController> {
  XYCoordinate<Length> getLowerBound();

  XYCoordinate<Length> getUpperBound();

  SampleAreaStage sampleAreaStage();
}
