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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.procedure;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ConditionPath;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.ResultPath;

public class InstructionDependents {
  private static final InstructionDependents EMPTY = new InstructionDependents();

  private final Set<ConditionPath<Absolute, ?>> conditionDependents;
  private final Set<ResultPath<Absolute, ?>> resultDependents;
  private final Set<ExperimentPath<Absolute>> orderingDependents;

  public static InstructionDependents empty() {
    return EMPTY;
  }

  private InstructionDependents() {
    conditionDependents = Set.of();
    resultDependents = Set.of();
    orderingDependents = Set.of();
  }

  private InstructionDependents(
      Set<ConditionPath<Absolute, ?>> conditionDependents,
      Set<ResultPath<Absolute, ?>> resultDependents,
      Set<ExperimentPath<Absolute>> orderingDependents) {
    this.conditionDependents = conditionDependents;
    this.resultDependents = resultDependents;
    this.orderingDependents = orderingDependents;
  }

  public Stream<ConditionPath<Absolute, ?>> getConditionDependents() {
    return conditionDependents.stream();
  }

  public Stream<ResultPath<Absolute, ?>> getResultDependents() {
    return resultDependents.stream();
  }

  public Stream<ExperimentPath<Absolute>> getOrderingDependents() {
    return orderingDependents.stream();
  }

  private static <T> Set<T> with(Set<T> dependents, T dependent) {
    var newDependents = new LinkedHashSet<>(dependents);
    newDependents.add(dependent);
    return newDependents;
  }

  InstructionDependents withConditionDependent(Class<?> production, ExperimentPath<Absolute> path) {
    return new InstructionDependents(
        with(conditionDependents, ProductPath.toCondition(path, production)),
        resultDependents,
        orderingDependents);
  }

  InstructionDependents withResultDependent(Class<?> production, ExperimentPath<Absolute> path) {
    return new InstructionDependents(
        conditionDependents,
        with(resultDependents, ProductPath.toResult(path, production)),
        orderingDependents);
  }

  InstructionDependents withOrderingDependent(ExperimentPath<Absolute> path) {
    return new InstructionDependents(
        conditionDependents,
        resultDependents,
        with(orderingDependents, path));
  }
}
