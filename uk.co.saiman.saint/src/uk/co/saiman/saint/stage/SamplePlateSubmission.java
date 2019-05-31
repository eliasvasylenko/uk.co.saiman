package uk.co.saiman.saint.stage;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.instrument.sample.SampleState;

public class SamplePlateSubmission {
  private final SamplePlateStageController stageControl;
  private final SamplePreparation samplePreparation;

  public SamplePlateSubmission(
      SamplePlateStageController stageControl,
      SamplePreparation samplePreparation) {
    this.stageControl = stageControl;
    this.samplePreparation = samplePreparation;
  }

  /**
   * Request analysis at the given sample location.
   * <p>
   * The device will initially be put into the
   * {@link SampleState#ANALYSIS_REQUESTED} state. The possible states to follow
   * from this request are either {@link SampleState#ANALYSIS_FAILED} or
   * {@link SampleState#ANALYSIS}.
   * 
   * @param location the location to analyze
   */
  public void requestAnalysisLocation(SampleArea location) {
    stageControl.requestAnalysis(location);
  }

  /**
   * Invocation blocks until the previous request is fulfilled, or until a failure
   * state is reached.
   * 
   * @return the state resulting from the previous request, one of
   *         {@link SampleState#EXCHANGE_FAILED}, {@link SampleState#EXCHANGE},
   *         {@link SampleState#ANALYSIS_FAILED}, or {@link SampleState#ANALYSIS}
   */
  public SampleState awaitRequest(long time, TimeUnit unit) {
    return stageControl.awaitRequest(time, unit);
  }

  public SamplePreparation samplePreparation() {
    return samplePreparation;
  }
}
