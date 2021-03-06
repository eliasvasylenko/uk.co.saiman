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
 * This file is part of uk.co.saiman.maldi.stage.msapex.
 *
 * uk.co.saiman.maldi.stage.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage.msapex;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_AREA;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_AREA_EXECUTOR;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.maldi.sample.SamplePlateSubmission;

public class AddMaldiSampleAreaStepsHandler {
  @Execute
  public void provideSteps(
      ExecutorService executors,
      Experiment experiment,
      ExperimentPath<Absolute> path,
      @Optional SampleAreaSelection sampleAreaSelection,
      Environment environment) {
    if (sampleAreaSelection == null) {
      return;
    }

    var areaExecutor = executors.getExecutor(SAMPLE_AREA_EXECUTOR);

    var parent = experiment.getStep(path).orElse(null);
    if (parent == null) {
      return;
    }

    var plate = parent.getVariables().get(SAMPLE_PLATE).orElse(null);
    if (plate == null) {
      return;
    }

    var areas = sampleAreaSelection.sampleAreas().collect(toList());
    for (var sampleArea : areas) {
      var stepId = ExperimentId.fromName(sampleArea.id());
      if (parent.getSubstep(stepId).isEmpty()) {
        var step = ExperimentStepDesign
            .define(stepId)
            .withExecutor(areaExecutor)
            .withVariables(new Variables(environment).with(SAMPLE_AREA, plate.persistSampleArea(sampleArea)));

        parent.attach(step);
      }
    }
  }

  @CanExecute
  public boolean canProvideSteps(ExecutorService executors, Experiment experiment, ExperimentPath<Absolute> path) {
    return executors.getExecutor(SAMPLE_AREA_EXECUTOR) != null && experiment
        .getStep(path)
        .filter(step -> step.getInstruction().preparesCondition(SamplePlateSubmission.class))
        .isPresent();
  }
}
