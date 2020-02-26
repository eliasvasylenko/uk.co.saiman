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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.executor.Evaluation;

public class InstructionDependencies {
  private final ExperimentPath<Absolute> path;
  private final Set<Dependency> dependenciesTo;
  private final Set<Dependency> dependenciesFrom;
  private final Map<Class<?>, Evaluation> evaluations;

  public InstructionDependencies(ExperimentPath<Absolute> path) {
    this.path = path;
    this.dependenciesTo = Set.of();
    this.dependenciesFrom = Set.of();
    this.evaluations = Map.of();
  }

  private InstructionDependencies(
      ExperimentPath<Absolute> path,
      Set<Dependency> dependenciesTo,
      Set<Dependency> dependenciesFrom,
      Map<Class<?>, Evaluation> evaluations) {
    this.path = path;
    this.dependenciesTo = dependenciesTo;
    this.dependenciesFrom = dependenciesFrom;
    this.evaluations = evaluations;
  }

  public Stream<Dependency> getDependenciesTo() {
    return dependenciesTo.stream();
  }

  public Stream<Dependency> getDependenciesFrom() {
    return dependenciesFrom.stream();
  }

  public Optional<Evaluation> getEvaluation(Class<?> type) {
    return Optional.ofNullable(evaluations.get(type));
  }

  InstructionDependencies withDependency(Dependency dependency) {
    var dependenciesTo = this.dependenciesTo;
    var dependenciesFrom = this.dependenciesFrom;

    if (dependency.to() == path) {
      dependenciesTo = new LinkedHashSet<>(this.dependenciesTo);
      dependenciesTo.add(dependency);

    }
    if (dependency.from() == path) {
      dependenciesFrom = new LinkedHashSet<>(this.dependenciesFrom);
      dependenciesFrom.add(dependency);
    }

    return new InstructionDependencies(path, dependenciesTo, dependenciesFrom, evaluations);
  }

  InstructionDependencies withEvaluation(Class<?> type, Evaluation evaluation) {
    var evaluations = new LinkedHashMap<>(this.evaluations);
    evaluations.put(type, evaluation);
    return new InstructionDependencies(path, dependenciesTo, dependenciesFrom, evaluations);
  }
}
