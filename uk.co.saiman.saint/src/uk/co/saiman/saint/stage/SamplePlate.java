package uk.co.saiman.saint.stage;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.measure.quantity.Length;

import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class SamplePlate {
  private final Map<String, SampleArea> sampleLocations;
  private final XYCoordinate<Length> barcodeLocation;

  public SamplePlate(
      Collection<? extends SampleArea> sampleLocations,
      XYCoordinate<Length> barcodeLocation) {
    this.sampleLocations = mapSampleLocations(sampleLocations);
    this.barcodeLocation = requireNonNull(barcodeLocation);
  }

  public SamplePlate(Collection<? extends SampleArea> sampleLocations) {
    this.sampleLocations = mapSampleLocations(sampleLocations);
    this.barcodeLocation = null;
  }

  private static Map<String, SampleArea> mapSampleLocations(
      Collection<? extends SampleArea> sampleLocations) {
    return requireNonNull(sampleLocations)
        .stream()
        .collect(toMap(SampleArea::id, identity(), throwingMerger(), LinkedHashMap::new));
  }

  public Stream<SampleArea> sampleAreas() {
    return sampleLocations.values().stream();
  }

  public Optional<SampleArea> sampleArea(String id) {
    return Optional.ofNullable(sampleLocations.get(id));
  }

  public Optional<XYCoordinate<Length>> barcodeLocation() {
    return Optional.ofNullable(barcodeLocation);
  }
}
