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
package uk.co.saiman.experiment.conductor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public class InstructionDependents {
  private final Map<Class<?>, Set<ExperimentPath<Absolute>>> conditionDependents;
  private final Map<Class<?>, Set<ExperimentPath<Absolute>>> resultDependents;

  public InstructionDependents() {
    conditionDependents = Map.of();
    resultDependents = Map.of();
  }

  private InstructionDependents(
      Map<Class<?>, Set<ExperimentPath<Absolute>>> conditionDependents,
      Map<Class<?>, Set<ExperimentPath<Absolute>>> resultDependents) {
    this.conditionDependents = conditionDependents;
    this.resultDependents = resultDependents;
  }

  public Stream<Class<?>> getConsumedConditions() {
    return conditionDependents.keySet().stream();
  }

  public Stream<Class<?>> getConsumedResults() {
    return resultDependents.keySet().stream();
  }

  public Stream<ExperimentPath<Absolute>> getConditionDependents(Class<?> production) {
    return conditionDependents.get(production).stream();
  }

  public Stream<ExperimentPath<Absolute>> getResultDependents(Class<?> production) {
    return resultDependents.get(production).stream();
  }

  private static Map<Class<?>, Set<ExperimentPath<Absolute>>> with(
      Map<Class<?>, Set<ExperimentPath<Absolute>>> dependents,
      Class<?> production,
      ExperimentPath<Absolute> path) {
    var newDependents = new HashMap<>(dependents);
    newDependents.computeIfAbsent(production, p -> new HashSet<>()).add(path);
    return null;
  }

  public InstructionDependents withConditionDependent(
      Class<?> production,
      ExperimentPath<Absolute> path) {
    return new InstructionDependents(with(conditionDependents, production, path), resultDependents);
  }

  public InstructionDependents withResultDependent(
      Class<?> production,
      ExperimentPath<Absolute> path) {
    return new InstructionDependents(conditionDependents, with(resultDependents, production, path));
  }

  public static InstructionDependents merge(
      InstructionDependents first,
      InstructionDependents second) {
    var resultDependents = new HashMap<>(first.resultDependents);
    for (var result : second.resultDependents.keySet()) {
      resultDependents.put(result, second.resultDependents.get(result));
    }
    var conditionDependents = new HashMap<>(first.conditionDependents);
    for (var condition : second.conditionDependents.keySet()) {
      conditionDependents.put(condition, second.conditionDependents.get(condition));
    }
    return new InstructionDependents(conditionDependents, resultDependents);
  }
}
