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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public class InstructionDependents {
  private static final InstructionDependents EMPTY = new InstructionDependents();

  private final Map<Class<?>, Set<ExperimentPath<Absolute>>> conditionDependents;
  private final Map<Class<?>, Set<ExperimentPath<Absolute>>> resultDependents;

  public static InstructionDependents empty() {
    return EMPTY;
  }

  private InstructionDependents() {
    conditionDependents = Map.of();
    resultDependents = Map.of();
  }

  private InstructionDependents(
      Map<Class<?>, Set<ExperimentPath<Absolute>>> conditionDependents,
      Map<Class<?>, Set<ExperimentPath<Absolute>>> resultDependents) {
    this.conditionDependents = conditionDependents;
    this.resultDependents = resultDependents;
  }

  public Stream<Class<?>> getConditionDependents() {
    return conditionDependents.keySet().stream();
  }

  public Stream<Class<?>> getResultDependents() {
    return resultDependents.keySet().stream();
  }

  public Stream<ExperimentPath<Absolute>> getConditionDependents(Class<?> production) {
    return Optional.ofNullable(conditionDependents.get(production)).stream().flatMap(Set::stream);
  }

  public Stream<ExperimentPath<Absolute>> getResultDependents(Class<?> production) {
    return Optional.ofNullable(resultDependents.get(production)).stream().flatMap(Set::stream);
  }

  private static <T, U> Map<T, Set<U>> with(Map<T, Set<U>> dependents, T production, U path) {
    var newDependents = new HashMap<>(dependents);
    newDependents.computeIfAbsent(production, p -> new LinkedHashSet<>()).add(path);
    return newDependents;
  }

  InstructionDependents withConditionDependent(Class<?> production, ExperimentPath<Absolute> path) {
    return new InstructionDependents(with(conditionDependents, production, path), resultDependents);
  }

  InstructionDependents withResultDependent(Class<?> production, ExperimentPath<Absolute> path) {
    return new InstructionDependents(conditionDependents, with(resultDependents, production, path));
  }
}
