package uk.co.saiman.saint.stage;

import uk.co.saiman.instrument.stage.XYStageController;

public interface SampleAreaStageController extends XYStageController {
  /**
   * Get the controller for the sample plate stage upon which the sample areas
   * navigated by this stage reside.
   * 
   * @return The sample plate stage controller which manages the exchange of
   *         sample areas for this stage.
   */
  SamplePlateStageController samplePlateStageController();
}
