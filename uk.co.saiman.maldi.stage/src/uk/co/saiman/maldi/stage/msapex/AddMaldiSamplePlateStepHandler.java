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
package uk.co.saiman.maldi.stage.msapex;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_AREA;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_AREA_EXECUTOR;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE_EXECUTOR;
import static uk.co.saiman.maldi.sample.MaldiSampleConstants.SAMPLE_PLATE_PREPARATION_ID;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePreparation;

public class AddMaldiSamplePlateStepHandler {
  @Execute
  public void provideSteps(
      ExecutorService executors,
      Experiment experiment,
      @Optional MaldiSamplePlate samplePlate,
      @Optional SampleAreaSelection sampleAreaSelection,
      GlobalEnvironment environment) {
    var plateExecutor = executors.getExecutor(SAMPLE_PLATE_EXECUTOR);
    var areaExecutor = executors.getExecutor(SAMPLE_AREA_EXECUTOR);

    var samplePreparation = new MaldiSamplePreparation(samplePlate);
    var id = samplePreparation.id();
    var plate = samplePreparation.plate();

    var step = StepDefinition
        .define(
            ExperimentId
                .nextAvailableFromName(
                    "Sample Plate",
                    experiment.getIndependentSteps().map(Step::getId).collect(toList())),
            plateExecutor,
            new Variables(environment)
                .with(SAMPLE_PLATE, plate)
                .with(SAMPLE_PLATE_PREPARATION_ID, id));

    if (sampleAreaSelection != null) {
      var areas = sampleAreaSelection.sampleAreas().collect(toList());
      for (var sampleArea : areas) {
        var stepId = ExperimentId.fromName(sampleArea.id());
        var substep = StepDefinition
            .define(stepId, areaExecutor)
            .withVariables(
                new Variables(environment).with(SAMPLE_AREA, plate.persistSampleArea(sampleArea)));
        step = step.withSubstep(substep);
      }
    }

    experiment.attach(step);
  }

  @CanExecute
  public boolean canProvideSteps(
      ExecutorService executors,
      Experiment experiment,
      @Optional ExperimentPath<Absolute> path) {
    return executors.getExecutor(SAMPLE_PLATE_EXECUTOR) != null
        && executors.getExecutor(SAMPLE_AREA_EXECUTOR) != null
        && (path == null || path.isEmpty());
  }
}
