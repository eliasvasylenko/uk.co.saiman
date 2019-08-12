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
 * This file is part of uk.co.saiman.maldi.spectrum.
 *
 * uk.co.saiman.maldi.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.spectrum.msapex;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Service;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.msapex.step.provider.StepProvider;
import uk.co.saiman.maldi.spectrum.MaldiSpectrumExecutor;

public class MaldiSpectrumExperimentStepProvider implements StepProvider {
  private final MaldiSpectrumExecutor spectrumExecutor;

  @Inject
  public MaldiSpectrumExperimentStepProvider(@Service MaldiSpectrumExecutor spectrumExecutor) {
    this.spectrumExecutor = spectrumExecutor;
  }

  @Override
  public boolean canProvideSteps(
      ExperimentDefinition experiment,
      ExperimentPath<Absolute> path,
      GlobalEnvironment environment) {
    return experiment
        .findSubstep(path)
        .filter(step -> step.productions().anyMatch(spectrumExecutor.samplePreparation()::equals))
        .isPresent();
  }

  @Override
  public Stream<StepDefinition> provideSteps(
      ExperimentDefinition experiment,
      ExperimentPath<Absolute> path,
      GlobalEnvironment environment) {
    return Stream.of(StepDefinition.define(ExperimentId.fromName("Spectrum"), spectrumExecutor));
  }
}
