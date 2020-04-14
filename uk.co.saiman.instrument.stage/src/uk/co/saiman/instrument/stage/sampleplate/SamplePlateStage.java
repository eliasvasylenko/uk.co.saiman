package uk.co.saiman.instrument.stage.sampleplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.saiman.experiment.sampleplate.SampleArea;
import uk.co.saiman.experiment.sampleplate.SamplePreparation;
import uk.co.saiman.instrument.DeviceImpl;
import uk.co.saiman.instrument.sample.Analysis;
import uk.co.saiman.instrument.sample.RequestedSampleState;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.observable.ObservableProperty;
import uk.co.saiman.observable.ObservableValue;

public abstract class SamplePlateStage<T extends SamplePreparation, U extends SamplePlateStageController<T>>
    extends DeviceImpl<U> implements Stage<SampleArea> {
  private final XYStage underlyingStage;
  private final XYStageController underlyingController;

  private final ObservableProperty<Optional<RequestedSampleState<SampleArea>>> requestedSampleState;
  private final ObservableProperty<SampleState<SampleArea>> sampleState;

  private final ObservableProperty<Optional<T>> expectedSamplePreparation;
  private final ObservableProperty<Optional<T>> assumedSamplePreparation;

  public SamplePlateStage(XYStage stage) throws InterruptedException, TimeoutException {
    this.underlyingStage = stage;
    this.underlyingController = stage.acquireControl(100, TimeUnit.MILLISECONDS);

    this.requestedSampleState = ObservableProperty.over(Optional.empty());
    this.sampleState = ObservableProperty.over(NullPointerException::new);

    this.expectedSamplePreparation = ObservableProperty.over(Optional.empty());
    this.assumedSamplePreparation = ObservableProperty.over(Optional.empty());
  }

  @Override
  protected void setDisposed() {
    super.setDisposed();
    underlyingController.close();
  }

  @Override
  public ObservableValue<Optional<RequestedSampleState<SampleArea>>> requestedSampleState() {
    return requestedSampleState;
  }

  @Override
  public ObservableValue<SampleState<SampleArea>> sampleState() {
    return sampleState;
  }

  public XYStage underlyingStage() {
    return underlyingStage;
  }

  XYStageController underlyingController() {
    return underlyingController;
  }

  @Override
  public boolean isPositionReachable(SampleArea location) {
    return assumedSamplePreparation
        .get()
        .map(p -> p.plate().containsSampleArea(location))
        .orElse(false);
  }

  @Override
  public ObservableValue<Optional<SampleArea>> samplePosition() {
    return sampleState
        .map(
            s -> s instanceof Analysis<?>
                ? Optional.of(((Analysis<SampleArea>) s).position())
                : Optional.empty());
  }

  /**
   * This is the sample preparation which is "expected" by the consumer of the
   * {@link SamplePlateStageController device controller}.
   * <p>
   * This is intended to signal which kind of sample they need in order to run an
   * experiment, for instance the type of sample plate, some kind of sample id, or
   * even a barcode.
   * <p>
   * If the expected sample preparation is not "compatible" with the
   * {@link #assumedSamplePreparation() assumed sample preparation} then an
   * {@link SamplePlateStageController#requestExchange() exchange} must be
   * performed.
   * 
   * @return an observable over the expected sample preparation
   */
  public ObservableValue<Optional<T>> expectedSamplePreparation() {
    return expectedSamplePreparation;
  }

  /**
   * This is a description of the sample preparation "assumed" to be present on
   * the stage.
   * <p>
   * The assumption is cleared upon successful completion of a sample exchange.
   * <p>
   * If an assumption is cleared, or if the current assumption is "compatible"
   * with the {@link #expectedSamplePreparation() expected sample preparation},
   * then the expected sample preparation will be assumped to be present, in which
   * case this value will be updated.
   * 
   * @return an observable over the expected sample preparation
   */
  public ObservableValue<Optional<T>> assumedSamplePreparation() {
    return assumedSamplePreparation;
  }

  public abstract XYStage sampleAreaStage();

  boolean expectSamplePreparation(T samplePreparation) {
    expectedSamplePreparation.set(Optional.of(samplePreparation));
    return assumeSamplePreparation(samplePreparation);
  }

  private boolean assumeSamplePreparation(T samplePreparation) {
    synchronized (assumedSamplePreparation) {
      boolean isCompatible = assumedSamplePreparation
          .get()
          .map(assumed -> assumed.isCompatible(samplePreparation))
          .orElse(true);
      if (isCompatible) {
        assumedSamplePreparation.set(Optional.of(samplePreparation));
      }
      return isCompatible;
    }
  }

  void clearExpectedSamplePreparation() {
    expectedSamplePreparation.set(Optional.empty());
  }

  void clearAssumedSamplePreparation() {
    assumedSamplePreparation.set(Optional.empty());
  }
}
