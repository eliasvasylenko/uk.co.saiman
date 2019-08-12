package uk.co.saiman.maldi.stage;

import static uk.co.saiman.state.Accessor.intAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import java.util.UUID;

import uk.co.saiman.experiment.dependency.source.Preparation;
import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.variables.Variable;

public final class MaldiStageConstants {
  private MaldiStageConstants() {}

  public static final Provision<SamplePlateStage> MALDI_SAMPLE_PLATE_DEVICE = new Provision<>(
      "uk.co.saiman.maldi.stage.plate.device.provision");
  public static final Provision<SamplePlateStageController> MALDI_SAMPLE_PLATE_CONTROLLER = new Provision<>(
      "uk.co.saiman.maldi.stage.plate.controller.provision");

  public static final Provision<SampleAreaStage> MALDI_SAMPLE_AREA_DEVICE = new Provision<>(
      "uk.co.saiman.maldi.stage.area.device.provision");
  public static final Provision<SampleAreaStageController> MALDI_SAMPLE_AREA_CONTROLLER = new Provision<>(
      "uk.co.saiman.maldi.stage.area.controller.provision");

  public static final Preparation<SamplePlateSubmission> PLATE_SUBMISSION = new Preparation<>(
      "uk.co.saiman.maldi.executor.platesubmission");

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
