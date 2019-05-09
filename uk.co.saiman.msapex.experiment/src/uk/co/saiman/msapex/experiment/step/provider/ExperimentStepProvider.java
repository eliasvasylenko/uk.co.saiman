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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.step.provider;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.procedure.Productions;
import uk.co.saiman.experiment.production.Dependency;
import uk.co.saiman.experiment.production.Nothing;
import uk.co.saiman.experiment.production.Product;
import uk.co.saiman.properties.Localized;

public interface ExperimentStepProvider<T extends Dependency> {
  Localized<String> name();

  Executor<T> executor();

  Stream<StepDefinition<T>> createSteps(DefineStep<T> defineStep);

  @SuppressWarnings("unchecked")
  default Optional<ExperimentStepProvider<? extends Product>> asDependent(Executor<?> executor) {
    return Productions
        .asDependent(executor(), executor)
        .map(c -> (ExperimentStepProvider<? extends Product>) this);
  }

  @SuppressWarnings("unchecked")
  default <S> Optional<ExperimentStepProvider<Nothing>> asIndependent() {
    return Productions.asIndependent(executor()).map(c -> (ExperimentStepProvider<Nothing>) this);
  }
}
