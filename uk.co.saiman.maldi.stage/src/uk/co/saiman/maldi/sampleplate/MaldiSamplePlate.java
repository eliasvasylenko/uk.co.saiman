package uk.co.saiman.maldi.sampleplate;

import java.util.Optional;

import javax.measure.quantity.Length;

import uk.co.saiman.experiment.sampleplate.SamplePlate;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.state.StateMap;

public interface MaldiSamplePlate extends SamplePlate {
  Optional<XYCoordinate<Length>> barcodeLocation();

  @Override
  MaldiSampleArea sampleArea(StateMap state);
}
