package uk.co.saiman.instrument.stage.composed;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.instrument.DeviceControlImpl;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.StageControl;

public class ComposedStageControl<T> extends DeviceControlImpl<ComposedStage<T, ?>>
    implements StageControl<T> {
  public ComposedStageControl(ComposedStage<T, ?> device, long timeout, TimeUnit unit) {
    super(device, timeout, unit);
  }

  @Override
  public SampleState requestExchange() {
    return getDevice().requestExchange();
  }

  @Override
  public SampleState requestAnalysis() {
    return getDevice().requestAnalysis();
  }

  @Override
  public SampleState requestAnalysisLocation(T location) {
    return getDevice().requestAnalysisLocation(location);
  }
}
