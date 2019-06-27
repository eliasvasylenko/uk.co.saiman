/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.saint.stage;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class SampleAreaHold {
  private final XYStageController stageControl;
  private final SamplePlate samplePlate;

  public SampleAreaHold(XYStageController stageControl, SamplePlate samplePlate) {
    this.stageControl = stageControl;
    this.samplePlate = samplePlate;
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
  public void requestAnalysisLocation(XYCoordinate<Length> location) {
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

  public SamplePlate samplePlate() {
    return samplePlate;
  }
}
