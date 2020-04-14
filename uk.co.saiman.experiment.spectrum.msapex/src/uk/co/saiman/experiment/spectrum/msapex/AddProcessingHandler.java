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
 * This file is part of uk.co.saiman.experiment.spectrum.msapex.
 *
 * uk.co.saiman.experiment.spectrum.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.spectrum.msapex;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.processing.Processing.PROCESSING_VARIABLE;
import static uk.co.saiman.experiment.spectrum.SpectrumProcessingExecutor.SPECTRUM_PROCESSING_EXECUTOR;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.variables.Variables;

public class AddProcessingHandler {
  @CanExecute
  boolean canExecute(
      ExecutorService executors,
      Experiment experiment,
      ExperimentPath<Absolute> path) {
    return executors.getExecutor(SPECTRUM_PROCESSING_EXECUTOR) != null
        && experiment.getStep(path).filter(step -> step.observes(Spectrum.class)).isPresent();
  }

  @Execute
  void execute(
      ExecutorService executors,
      Experiment experiment,
      ExperimentPath<Absolute> path,
      Environment environment) {
    var processingExecutor = executors.getExecutor(SPECTRUM_PROCESSING_EXECUTOR);

    var parent = experiment.getStep(path).orElse(null);
    if (parent == null) {
      return;
    }

    var id = ExperimentId
        .nextAvailableFromName(
            "Spectrum Processing",
            parent.getDependentSteps().map(Step::getId).collect(toList()));

    var step = StepDefinition
        .define(
            id,
            processingExecutor,
            new Variables(environment).with(PROCESSING_VARIABLE, new Processing()));

    parent.attach(step);
  }
}
