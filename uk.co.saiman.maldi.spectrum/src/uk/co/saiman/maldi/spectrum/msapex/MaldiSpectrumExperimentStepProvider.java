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

import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.environment.SharedEnvironment;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.msapex.step.provider.DefineStep;
import uk.co.saiman.experiment.msapex.step.provider.StepProvider;
import uk.co.saiman.maldi.spectrum.MaldiSpectrumExecutor;
import uk.co.saiman.maldi.spectrum.i18n.MaldiSpectrumProperties;
import uk.co.saiman.properties.PropertyLoader;

public class MaldiSpectrumExperimentStepProvider implements StepProvider {
  private final MaldiSpectrumProperties properties;
  private final MaldiSpectrumExecutor spectrumExecutor;

  @Inject
  public MaldiSpectrumExperimentStepProvider(
      @Service PropertyLoader properties,
      @Service MaldiSpectrumExecutor spectrumExecutor) {
    this.properties = properties.getProperties(MaldiSpectrumProperties.class);
    this.spectrumExecutor = spectrumExecutor;
  }

  @Override
  public Executor executor() {
    return spectrumExecutor;
  }

  @Override
  public Stream<StepDefinition> createSteps(SharedEnvironment environment, DefineStep defineStep) {
    return Stream.of(defineStep.withName("Spectrum"));
  }
}
