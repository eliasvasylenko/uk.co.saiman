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
 * This file is part of uk.co.saiman.experiment.definition.
 *
 * uk.co.saiman.experiment.definition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.definition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.definition;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.graph.ExperimentPath;
import uk.co.saiman.experiment.graph.ExperimentPath.Absolute;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.production.Nothing;
import uk.co.saiman.experiment.requirement.Requirement;

public class ExperimentDefinition extends StepContainer<Absolute, ExperimentDefinition> {
  private final ExperimentId id;
  private Procedure procedure;

  private ExperimentDefinition(ExperimentId id, List<StepDefinition<?>> steps) {
    super(steps);
    this.id = id;
  }

  private ExperimentDefinition(
      ExperimentId id,
      List<StepDefinition<?>> steps,
      Map<ExperimentId, StepDefinition<?>> dependents) {
    super(steps, dependents);
    this.id = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj))
      return false;

    ExperimentDefinition that = (ExperimentDefinition) obj;

    return Objects.equals(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, super.hashCode());
  }

  public static ExperimentDefinition define(ExperimentId id) {
    return new ExperimentDefinition(id, List.of(), Map.of());
  }

  public ExperimentId id() {
    return id;
  }

  public ExperimentDefinition withId(ExperimentId id) {
    return new ExperimentDefinition(id, getSteps(), getDependents());
  }

  @Override
  ExperimentDefinition with(
      List<StepDefinition<?>> steps,
      Map<ExperimentId, StepDefinition<?>> dependents) {
    return new ExperimentDefinition(id, steps, dependents);
  }

  @Override
  ExperimentDefinition with(List<StepDefinition<?>> steps) {
    return new ExperimentDefinition(id, steps);
  }

  @SuppressWarnings("unchecked")
  public Stream<StepDefinition<Nothing>> independentSteps() {
    return substeps()
        .filter(i -> i.executor().directRequirement().equals(Requirement.none()))
        .map(i -> (StepDefinition<Nothing>) i);
  }

  public Procedure procedure() {
    if (procedure == null) {
      procedure = new Procedure(id, closure(this));
    }
    return procedure;
  }

  private List<Instruction<?>> closure(StepContainer<?, ?> steps) {
    return steps
        .substeps()
        .flatMap(step -> closure(step, ExperimentPath.defineAbsolute()))
        .collect(toList());
  }

  private Stream<Instruction<?>> closure(StepDefinition<?> step, ExperimentPath<Absolute> path) {
    var p = path.resolve(step.id());
    return Stream
        .concat(
            Stream.of(new Instruction<>(p, step.variables(), step.executor())),
            step.substeps().flatMap(s -> closure(s, p)));
  }
}
