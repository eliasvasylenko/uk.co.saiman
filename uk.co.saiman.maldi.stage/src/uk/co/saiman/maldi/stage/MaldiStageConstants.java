package uk.co.saiman.maldi.stage;

import static uk.co.saiman.state.Accessor.intAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.UUID;

import uk.co.saiman.experiment.variables.Variable;

public final class MaldiStageConstants {
  private MaldiStageConstants() {}

  public static final Variable<String> SAMPLE_WELL_ID = new Variable<>(
      "uk.co.saiman.maldi.variable.samplewell.id",
      stringAccessor());

  public static final Variable<String> SAMPLE_PLATE_ID = new Variable<>(
      "uk.co.saiman.maldi.variable.sampleplate.id",
      stringAccessor());
  public static final Variable<UUID> SAMPLE_PLATE_PREPARATION_ID = new Variable<>(
      "uk.co.saiman.maldi.variable.sampleplate.preparationid",
      stringAccessor().map(UUID::fromString, UUID::toString));
  public static final Variable<Integer> SAMPLE_PLATE_BARCODE = new Variable<>(
      "uk.co.saiman.maldi.variable.sampleplate.barcode",
      intAccessor());

}
