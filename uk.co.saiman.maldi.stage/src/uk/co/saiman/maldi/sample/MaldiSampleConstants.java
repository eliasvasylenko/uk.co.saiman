package uk.co.saiman.maldi.sample;

import static uk.co.saiman.state.Accessor.intAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.Optional;
import java.util.UUID;

import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlateIndex;
import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.StateMap;

public final class MaldiSampleConstants {
  private MaldiSampleConstants() {}

  public static final String SAMPLE_AREA_EXECUTOR = "uk.co.saiman.maldi.executor.samplearea";

  public static final String SAMPLE_AREA_ID = "uk.co.saiman.maldi.variable.samplewell.id";
  public static final Variable<StateMap> SAMPLE_AREA = new Variable<>(
      SAMPLE_AREA_ID,
      Accessor.mapAccessor());

  public static final String SAMPLE_PLATE_EXECUTOR = "uk.co.saiman.maldi.executor.sampleplate";

  public static final String SAMPLE_PLATE_ID = "uk.co.saiman.maldi.variable.sampleplate.id";
  public static final Variable<MaldiSamplePlate> SAMPLE_PLATE = new Variable<>(
      SAMPLE_PLATE_ID,
      globals -> {
        var plateIndex = globals.provideValue(MaldiSamplePlateIndex.class);
        return stringAccessor()
            .map(id -> plateIndex.getSamplePlate(id).get(), plate -> plateIndex.getId(plate).get());
      });

  public static final String SAMPLE_PLATE_PREPARATION_ID_ID = "uk.co.saiman.maldi.variable.sampleplate.preparationid";
  public static final Variable<UUID> SAMPLE_PLATE_PREPARATION_ID = new Variable<>(
      SAMPLE_PLATE_PREPARATION_ID_ID,
      stringAccessor().map(UUID::fromString, UUID::toString));

  public static final String SAMPLE_PLATE_BARCODE_ID = "uk.co.saiman.maldi.variable.sampleplate.barcode";
  public static final Variable<Optional<Integer>> SAMPLE_PLATE_BARCODE = new Variable<>(
      SAMPLE_PLATE_BARCODE_ID,
      intAccessor().toOptionalAccessor());
}
