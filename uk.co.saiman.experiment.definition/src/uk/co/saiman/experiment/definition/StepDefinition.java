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

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Dependency;
import uk.co.saiman.experiment.production.Product;
import uk.co.saiman.experiment.production.Production;
import uk.co.saiman.experiment.requirement.Requirement;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.Variables;

public class StepDefinition<T extends Dependency> extends StepContainer<StepDefinition<T>> {
  private final String id;
  private final Variables variables;
  private final Executor<T> executor;
  private final Plan plan;

  private StepDefinition(
      String id,
      Variables variables,
      Executor<T> executor,
      Plan plan,
      List<StepDefinition<?>> steps) {
    super(steps);
    this.id = id;
    this.variables = variables;
    this.executor = executor;
    this.plan = plan;
  }

  private StepDefinition(
      String id,
      Variables variables,
      Executor<T> process,
      Plan plan,
      List<StepDefinition<?>> steps,
      Map<String, StepDefinition<?>> dependents) {
    super(steps, dependents);
    this.id = id;
    this.variables = variables;
    this.executor = process;
    this.plan = plan;
  }

  public static <T extends Dependency> StepDefinition<T> define(
      String id,
      Variables variables,
      Executor<T> process,
      Plan plan) {
    return new StepDefinition<>(
        ExperimentDefinition.validateName(id),
        requireNonNull(variables),
        requireNonNull(process),
        plan,
        List.of(),
        Map.of());
  }

  public String id() {
    return id;
  }

  public StepDefinition<T> withId(String id) {
    return new StepDefinition<>(
        ExperimentDefinition.validateName(id),
        variables,
        executor,
        plan,
        getSteps(),
        getDependents());
  }

  public Variables variables() {
    return variables;
  }

  public StepDefinition<T> withVariables(Variables variables) {
    return new StepDefinition<>(id, variables, executor, plan, getSteps(), getDependents());
  }

  public <U> Optional<U> variable(Variable<U> variable) {
    return variables.get(variable);
  }

  public <U> StepDefinition<T> withVariable(Variable<U> variable, U value) {
    return withVariables(variables.with(variable, value));
  }

  public <U> StepDefinition<T> withVariable(Variable<U> variable, Function<Optional<U>, U> value) {
    return withVariables(variables.with(variable, value));
  }

  public Executor<T> executor() {
    return executor;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj))
      return false;

    StepDefinition<?> that = (StepDefinition<?>) obj;

    return Objects.equals(this.id, that.id)
        && Objects.equals(this.variables, that.variables)
        && Objects.equals(this.executor, that.executor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, variables, executor, super.hashCode());
  }

  @Override
  StepDefinition<T> with(List<StepDefinition<?>> steps, Map<String, StepDefinition<?>> dependents) {
    return new StepDefinition<>(id, variables, executor, plan, steps, dependents);
  }

  @Override
  StepDefinition<T> with(List<StepDefinition<?>> steps) {
    return new StepDefinition<>(id, variables, executor, plan, steps);
  }

  @SuppressWarnings("unchecked")
  public <U extends Product> Stream<StepDefinition<U>> dependentSteps(Production<U> production) {
    return steps()
        .filter(i -> i.executor().directRequirement().equals(Requirement.on(production)))
        .map(i -> (StepDefinition<U>) i);
  }

  public StepDefinition<T> withPlan(Plan plan) {
    return new StepDefinition<>(id, variables, executor, plan, getSteps(), getDependents());
  }

  public Plan getPlan() {
    return plan;
  }
}
