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
 * This file is part of uk.co.saiman.experiment.sample.
 *
 * uk.co.saiman.experiment.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.sample;

import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Nothing;
import uk.co.saiman.experiment.environment.Provision;
import uk.co.saiman.experiment.instruction.ExecutionContext;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Preparation;
import uk.co.saiman.experiment.production.Production;
import uk.co.saiman.experiment.requirement.AdditionalRequirement;
import uk.co.saiman.experiment.requirement.NoRequirement;
import uk.co.saiman.experiment.requirement.Requirement;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.instrument.sample.SampleController;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
public interface SampleExecutor<T> extends Executor<Nothing> {
  Variable<T> sampleLocation();

  Preparation<Void> samplePreparation();

  Provision<? extends SampleController<T>> sampleDevice();

  @Override
  default void execute(ExecutionContext<Nothing> context) {
    var location = context.getVariable(sampleLocation());
    var controller = context.acquireResource(sampleDevice());

    controller.requestAnalysis(location);
    context.prepareCondition(samplePreparation(), null);
  }

  @Override
  default Stream<Production<?>> products() {
    return Stream.of(samplePreparation());
  }

  @Override
  default NoRequirement mainRequirement() {
    return Requirement.none();
  }

  @Override
  default Stream<AdditionalRequirement<?>> additionalRequirements() {
    return Stream.empty();
  }

  @Override
  default Stream<VariableDeclaration> variables() {
    return Stream.of(sampleLocation().declareRequired());
  }
}
