package uk.co.saiman.maldi.sampleplate;

import java.util.Optional;
import java.util.stream.Stream;

public interface MaldiSamplePlateIndex {
  String SAMPLE_PLATE_ID = "uk.co.saiman.maldi.sampleplate.id";

  Stream<MaldiSamplePlate> getSamplePlates();

  Optional<MaldiSamplePlate> getSamplePlate(String id);

  Optional<String> getId(MaldiSamplePlate samplePlate);
}
