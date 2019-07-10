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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import static java.util.stream.IntStream.iterate;

import java.util.function.Predicate;

import uk.co.saiman.experiment.definition.StepContainer;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.dependency.Dependency;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.msapex.step.provider.DefineStep;

public class DefineStepImpl<T extends Dependency> implements DefineStep<T> {
  private final Predicate<ExperimentId> validate;
  private final Executor<T> executor;

  public DefineStepImpl(StepContainer<?, ?> target, Executor<T> executor) {
    this(id -> target.findSubstep(id).isEmpty(), executor);
  }

  protected DefineStepImpl(Predicate<ExperimentId> validate, Executor<T> executor) {
    this.validate = validate;
    this.executor = executor;
  }

  @Override
  public StepDefinition<T> withId(ExperimentId id) {
    if (validate.test(id)) {
      return StepDefinition.define(id, executor);
    }

    return StepDefinition
        .define(
            iterate(0, i -> ++i)
                .mapToObj(i -> ExperimentId.fromName(id.name() + " " + i))
                .filter(validate)
                .findFirst()
                .get(),
            executor);
  }
}
