package uk.co.saiman.saint.stage;

import java.util.Optional;

// TODO record & value type
public class SamplePreparation {
  private final String id;
  private final SamplePlate plate;
  private final Integer barcode;

  public SamplePreparation(String id, SamplePlate plate, Integer barcode) {
    this.id = id;
    this.plate = plate;
    this.barcode = barcode;
  }

  public Optional<String> id() {
    return Optional.ofNullable(id);
  }

  public SamplePlate plate() {
    return plate;
  }

  public Optional<Integer> barcode() {
    return Optional.ofNullable(barcode);
  }
}
