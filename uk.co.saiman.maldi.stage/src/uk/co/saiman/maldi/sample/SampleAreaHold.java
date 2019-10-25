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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.sample;

import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;

import uk.co.saiman.instrument.stage.XYStageController;
import uk.co.saiman.maldi.sampleplate.MaldiSampleArea;
import uk.co.saiman.maldi.stage.MaldiStageController;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class SampleAreaHold {
  private final XYStageController stageControl;
  private final MaldiSampleArea sampleArea;

  public SampleAreaHold(MaldiStageController stageControl, MaldiSampleArea sampleArea) {
    this.stageControl = stageControl;
    this.sampleArea = sampleArea;
  }

  public void setAnalysisLocation(XYCoordinate<Length> offset, long time, TimeUnit unit) {
    if (!sampleArea.isLocationReachable(offset)) {
      throw new OffsetUnreachableException(offset);
    }
    stageControl.requestAnalysis(offset.add(sampleArea.center()));
    stageControl.awaitRequest(time, unit);
  }

  public MaldiSampleArea sampleArea() {
    return sampleArea;
  }
}
