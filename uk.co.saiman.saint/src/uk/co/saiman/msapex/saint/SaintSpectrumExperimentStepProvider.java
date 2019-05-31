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
package uk.co.saiman.msapex.saint;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Service;

import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Condition;
import uk.co.saiman.msapex.experiment.step.provider.DefineStep;
import uk.co.saiman.msapex.experiment.step.provider.StepProvider;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.saint.SaintProperties;
import uk.co.saiman.saint.spectrum.SaintSpectrumExecutor;

public class SaintSpectrumExperimentStepProvider implements StepProvider<Condition<Void>> {
  private final SaintProperties properties;
  private final SaintSpectrumExecutor spectrumExecutor;

  @Inject
  public SaintSpectrumExperimentStepProvider(
      @Service PropertyLoader properties,
      @Service SaintSpectrumExecutor spectrumExecutor) {
    this.properties = properties.getProperties(SaintProperties.class);
    this.spectrumExecutor = spectrumExecutor;
  }

  @Override
  public Executor<Condition<Void>> executor() {
    return spectrumExecutor;
  }

  @Override
  public Stream<StepDefinition<Condition<Void>>> createSteps(
      DefineStep<Condition<Void>> defineStep) {
    return Stream.of(defineStep.withName("Spectrum"));
  }
}
