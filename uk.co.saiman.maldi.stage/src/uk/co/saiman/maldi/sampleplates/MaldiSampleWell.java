package uk.co.saiman.maldi.sampleplates;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.experiment.sampleplate.SampleCircle;
import uk.co.saiman.maldi.sampleplate.MaldiSampleArea;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class MaldiSampleWell extends SampleCircle implements MaldiSampleArea {
  public MaldiSampleWell(String id, XYCoordinate<Length> center, Quantity<Length> radius) {
    super(id, center, radius);
  }
}
