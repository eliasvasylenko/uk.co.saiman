package uk.co.saiman.instrument.stage.sampleplate;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.experiment.sampleplate.SampleArea;
import uk.co.saiman.experiment.sampleplate.SamplePreparation;
import uk.co.saiman.instrument.DeviceImpl.ControlContext;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.StageController;

public class SamplePlateStageController<T extends SamplePreparation>
    implements StageController<SampleArea> {
  private final SamplePlateStage<T, ?> samplePlateStage;

  private SampleArea request;

  private final ControlContext context;

  public SamplePlateStageController(
      SamplePlateStage<T, ?> samplePlateStage,
      ControlContext context) {
    this.samplePlateStage = samplePlateStage;
    this.context = context;
  }

  @Override
  public void withdrawRequest() {
    try (var lock = context.acquireLock()) {
      this.samplePlateStage.underlyingController().withdrawRequest();
    }
  }

  @Override
  public void requestExchange() {
    try (var lock = context.acquireLock()) {
      this.samplePlateStage.underlyingController().requestExchange();
    }
  }

  @Override
  public void requestReady() {
    try (var lock = context.acquireLock()) {
      this.samplePlateStage.underlyingController().requestReady();
    }
  }

  @Override
  public void requestAnalysis(SampleArea position) {
    try (var lock = context.acquireLock()) {
      this.request = position;
      this.samplePlateStage.underlyingController().requestAnalysis(position.rest());
    }
  }

  @Override
  public SampleState<SampleArea> awaitRequest(long timeout, TimeUnit unit) {
    try (var lock = context.acquireLock()) {
      var state = this.samplePlateStage.underlyingController().awaitRequest(timeout, unit);
      return SampleState.map(state, t -> request);
    }
  }

  @Override
  public SampleState<SampleArea> awaitReady(long timeout, TimeUnit unit) {
    try (var lock = context.acquireLock()) {
      var state = this.samplePlateStage.underlyingController().awaitReady(timeout, unit);
      return SampleState.map(state, t -> null);
    }
  }

  @Override
  public void close() {
    context.close();
  }

  @Override
  public boolean isOpen() {
    return context.isOpen();
  }

  public boolean expectSamplePreparation(T samplePreparation) {
    try (var lock = context.acquireLock()) {
      return samplePlateStage.expectSamplePreparation(samplePreparation);
    }
  }

  public void clearExpectedSamplePreparation() {
    try (var lock = context.acquireLock()) {
      samplePlateStage.clearExpectedSamplePreparation();
    }
  }

  public void clearAssumedSamplePreparation() {
    try (var lock = context.acquireLock()) {
      samplePlateStage.clearAssumedSamplePreparation();
    }
  }
}