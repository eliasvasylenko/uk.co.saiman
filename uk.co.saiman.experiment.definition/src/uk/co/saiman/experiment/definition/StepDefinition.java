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
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Dependency;
import uk.co.saiman.experiment.environment.StaticEnvironment;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.graph.ExperimentPath.Relative;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Product;
import uk.co.saiman.experiment.production.Production;
import uk.co.saiman.experiment.requirement.Requirement;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.state.StateMap;

public class StepDefinition<T extends Dependency>
    extends StepContainer<Relative, StepDefinition<T>> {
  private final ExperimentId id;
  private final StateMap variableMap;
  private final Executor<T> executor;
  private final Plan plan;

  private StepDefinition(
      ExperimentId id,
      Executor<T> executor,
      StateMap variableMap,
      Plan plan,
      List<StepDefinition<?>> steps) {
    super(steps);
    this.id = id;
    this.variableMap = variableMap;
    this.executor = executor;
    this.plan = plan;
  }

  private StepDefinition(
      ExperimentId id,
      Executor<T> executor,
      StateMap variableMap,
      Plan plan,
      List<StepDefinition<?>> steps,
      Map<ExperimentId, StepDefinition<?>> dependents) {
    super(steps, dependents);
    this.id = id;
    this.variableMap = variableMap;
    this.executor = executor;
    this.plan = plan;
  }

  public static <T extends Dependency> StepDefinition<T> define(
      ExperimentId id,
      Executor<T> executor) {
    return define(id, executor, StateMap.empty());
  }

  public static <T extends Dependency> StepDefinition<T> define(
      ExperimentId id,
      Executor<T> executor,
      Variables variables) {
    return define(id, executor, variables.state());
  }

  private static <T extends Dependency> StepDefinition<T> define(
      ExperimentId id,
      Executor<T> executor,
      StateMap variableMap) {
    return new StepDefinition<>(
        requireNonNull(id),
        requireNonNull(executor),
        requireNonNull(variableMap),
        Plan.WITHHOLD,
        List.of(),
        Map.of());
  }

  public ExperimentId id() {
    return id;
  }

  public StepDefinition<T> withId(ExperimentId id) {
    return new StepDefinition<>(
        requireNonNull(id),
        executor,
        variableMap,
        plan,
        getSteps(),
        getDependents());
  }

  public StateMap variableMap() {
    return variableMap;
  }

  public StepDefinition<T> withVariableMap(StateMap variableMap) {
    return new StepDefinition<>(id, executor, variableMap, plan, getSteps(), getDependents());
  }

  public Variables variables(StaticEnvironment environment) {
    return new Variables(environment, variableMap);
  }

  public StepDefinition<T> withVariables(Variables variables) {
    return new StepDefinition<>(id, executor, variables.state(), plan, getSteps(), getDependents());
  }

  public StepDefinition<T> withVariables(
      StaticEnvironment environment,
      Function<? super Variables, ? extends Variables> update) {
    return new StepDefinition<>(
        id,
        executor,
        update.apply(variables(environment)).state(),
        plan,
        getSteps(),
        getDependents());
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
        && Objects.equals(this.variableMap, that.variableMap)
        && Objects.equals(this.executor, that.executor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, variableMap, executor, super.hashCode());
  }

  @Override
  StepDefinition<T> with(
      List<StepDefinition<?>> steps,
      Map<ExperimentId, StepDefinition<?>> dependents) {
    return new StepDefinition<>(id, executor, variableMap, plan, steps, dependents);
  }

  @Override
  StepDefinition<T> with(List<StepDefinition<?>> steps) {
    return new StepDefinition<>(id, executor, variableMap, plan, steps);
  }

  @SuppressWarnings("unchecked")
  public <U extends Product> Stream<StepDefinition<U>> dependentSteps(Production<U> production) {
    return substeps()
        .filter(i -> i.executor().mainRequirement().equals(Requirement.on(production)))
        .map(i -> (StepDefinition<U>) i);
  }

  public StepDefinition<T> withPlan(Plan plan) {
    return new StepDefinition<>(id, executor, variableMap, plan, getSteps(), getDependents());
  }

  public Plan getPlan() {
    return plan;
  }
}
