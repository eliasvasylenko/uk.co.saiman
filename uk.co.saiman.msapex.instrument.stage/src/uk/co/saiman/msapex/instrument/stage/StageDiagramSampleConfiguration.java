package uk.co.saiman.msapex.instrument.stage;

import java.util.stream.Stream;

import javax.measure.Quantity;

public interface StageDiagramSampleConfiguration {
  String getName();

  Stream<Quantity<?>> getCoordinates();
}
