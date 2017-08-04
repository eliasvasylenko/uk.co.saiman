package uk.co.saiman.instrument.stage.msapex;

import java.util.stream.Stream;

import javax.measure.Quantity;

public interface StageDiagramSampleConfiguration {
  String getName();

  Stream<Quantity<?>> getCoordinates();
}
