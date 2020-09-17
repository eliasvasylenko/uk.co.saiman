/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.maldi.sampleplate.MaldiSampleArea;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePreparation;
import uk.co.saiman.maldi.stage.MaldiStage;
import uk.co.saiman.maldi.stage.MaldiStageController;

public class SamplePlateSubmission {
  private final MaldiStage stage;
  private final MaldiStageController stageControl;
  private final MaldiSamplePreparation samplePreparation;

  public SamplePlateSubmission(
      MaldiStage stage,
      MaldiStageController stageControl,
      MaldiSamplePreparation samplePreparation) {
    this.stage = stage;
    this.stageControl = stageControl;
    this.samplePreparation = samplePreparation;
  }

  public XYStage beginAnalysis(MaldiSampleArea location, long time, TimeUnit unit) {
    stageControl.requestAnalysis(location);
    stageControl.awaitRequest(time, unit);
    return stage.sampleAreaStage();
  }

  public MaldiSamplePreparation samplePreparation() {
    return samplePreparation;
  }
}
