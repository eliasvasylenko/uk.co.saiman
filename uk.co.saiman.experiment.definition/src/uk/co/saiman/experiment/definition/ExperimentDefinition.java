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
package uk.co.saiman.experiment.definition;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.production.Nothing;
import uk.co.saiman.experiment.requirement.Requirement;

public class ExperimentDefinition extends StepContainer<ExperimentDefinition> {
  private final String id;
  private Procedure procedure;

  private ExperimentDefinition(String id, List<StepDefinition<?>> steps) {
    super(steps);
    this.id = validateName(id);
  }

  private ExperimentDefinition(
      String id,
      List<StepDefinition<?>> steps,
      Map<String, StepDefinition<?>> dependents) {
    super(steps, dependents);
    this.id = validateName(id);
  }

  public static ExperimentDefinition define(String id) {
    return new ExperimentDefinition(id, List.of(), Map.of());
  }

  public String id() {
    return id;
  }

  public ExperimentDefinition withId(String id) {
    return new ExperimentDefinition(id, getSteps(), getDependents());
  }

  static String validateName(String name) {
    if (!isNameValid(name)) {
      throw new ExperimentDefinitionException(format("Invalid name for experiment %s", name));
    }
    return name;
  }

  public static boolean isNameValid(String name) {
    final String ALPHANUMERIC = "[a-zA-Z0-9]+";
    final String DIVIDER_CHARACTERS = "[ \\.\\-_]+";

    return name != null
        && name.matches(ALPHANUMERIC + "(" + DIVIDER_CHARACTERS + ALPHANUMERIC + ")*");
  }

  public Optional<StepDefinition<?>> findStep(ExperimentPath<?> path) {
    path = path.toAbsolute();
    if (path.isEmpty()) {
      return Optional.empty();
    }

    var ids = path.iterator();
    var step = findStep(ids.next());
    while (ids.hasNext() && step.isPresent()) {
      step = step.flatMap(s -> s.findStep(ids.next()));
    }
    return step;
  }

  @Override
  ExperimentDefinition with(
      List<StepDefinition<?>> steps,
      Map<String, StepDefinition<?>> dependents) {
    return new ExperimentDefinition(id, steps, dependents);
  }

  @Override
  ExperimentDefinition with(List<StepDefinition<?>> steps) {
    return new ExperimentDefinition(id, steps);
  }

  @SuppressWarnings("unchecked")
  public Stream<StepDefinition<Nothing>> independentSteps() {
    return steps()
        .filter(i -> i.executor().directRequirement().equals(Requirement.none()))
        .map(i -> (StepDefinition<Nothing>) i);
  }

  public Procedure procedure() {
    if (procedure == null) {
      procedure = new Procedure(id, closure(this));
    }
    return procedure;
  }

  private List<Instruction<?>> closure(StepContainer<?> steps) {
    return steps
        .steps()
        .flatMap(step -> closure(step, ExperimentPath.defineAbsolute()))
        .collect(toList());
  }

  private Stream<Instruction<?>> closure(StepDefinition<?> step, ExperimentPath<Absolute> path) {
    var p = path.resolve(step.id());
    return Stream
        .concat(
            Stream.of(new Instruction<>(step.id(), step.variables(), step.executor(), p)),
            step.steps().flatMap(s -> closure(s, p)));
  }
}
