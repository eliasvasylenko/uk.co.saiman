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
package uk.co.saiman.maldi.sample.msapex;

import static uk.co.saiman.experiment.sample.XYStageExecutor.LOCATION;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Service;

import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.environment.StaticEnvironment;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.msapex.step.provider.DefineStep;
import uk.co.saiman.experiment.msapex.step.provider.StepProvider;
import uk.co.saiman.experiment.production.Condition;
import uk.co.saiman.maldi.sample.MaldiSampleAreaExecutor;
import uk.co.saiman.maldi.stage.SamplePlateSubmission;
import uk.co.saiman.maldi.stage.msapex.MaldiStageDiagram;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.measurement.coordinate.XYCoordinate;

public class MaldiSampleAreaExperimentStepProvider
    implements StepProvider<Condition<SamplePlateSubmission>> {
  private final MaldiStageDiagram stageDiagram;
  private final MaldiSampleAreaExecutor stageExecutor;

  @Inject
  public MaldiSampleAreaExperimentStepProvider(
      @Service MaldiStageDiagram stageDiagram,
      @Service MaldiSampleAreaExecutor stageExecutor) {
    this.stageDiagram = stageDiagram;
    this.stageExecutor = stageExecutor;
  }

  @Override
  public Executor<Condition<SamplePlateSubmission>> executor() {
    return stageExecutor;
  }

  @Override
  public Stream<StepDefinition<Condition<SamplePlateSubmission>>> createSteps(
      StaticEnvironment environment,
      DefineStep<Condition<SamplePlateSubmission>> defineStep) {
    // TODO set location from stage diagram, e.g. selected wells
    var step = defineStep
        .withName("Sample Position")
        .withVariables(
            environment,
            v -> v.with(LOCATION, new XYCoordinate<>(Units.metre().getUnit(), 0, 0)));
    return Stream.of(step);
  }
}
