package uk.co.saiman.saint.stage;

import uk.co.saiman.instrument.stage.XYStage;

public interface SampleAreaStage extends XYStage<SampleAreaStageController> {
  /**
   * Get the sample plate stage upon which the sample areas navigated by this
   * stage reside.
   * 
   * @return The sample plate stage which manages the exchange of sample areas for
   *         this stage.
   */
  SamplePlateStage samplePlateStage();
}
