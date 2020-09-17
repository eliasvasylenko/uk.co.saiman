/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import static uk.co.saiman.experiment.definition.ExecutionPlan.EXECUTE;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath.Relative;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.state.StateMap;

public class StepDefinition extends Definition<Relative, StepDefinition> {
  private final ExperimentId id;
  private final StateMap variableMap;
  private final Executor executor;
  private final ExecutionPlan plan;

  private StepDefinition(
      ExperimentId id,
      Executor executor,
      StateMap variableMap,
      ExecutionPlan plan,
      List<StepDefinition> steps,
      Map<ExperimentId, StepDefinition> dependents) {
    super(steps, dependents);
    this.id = id;
    this.variableMap = variableMap;
    this.executor = executor;
    this.plan = steps.stream().map(StepDefinition::getPlan).anyMatch(EXECUTE::equals)
        ? EXECUTE
        : plan;
  }

  public static StepDefinition define(ExperimentId id, Executor executor) {
    return define(id, executor, StateMap.empty());
  }

  public static StepDefinition define(ExperimentId id, Executor executor, Variables variables) {
    return define(id, executor, variables.state());
  }

  private static StepDefinition define(ExperimentId id, Executor executor, StateMap variableMap) {
    return new StepDefinition(
        requireNonNull(id),
        requireNonNull(executor),
        requireNonNull(variableMap),
        ExecutionPlan.WITHHOLD,
        List.of(),
        Map.of());
  }

  public ExperimentId id() {
    return id;
  }

  public StepDefinition withId(ExperimentId id) {
    return new StepDefinition(
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

  public StepDefinition withVariableMap(StateMap variableMap) {
    return new StepDefinition(id, executor, variableMap, plan, getSteps(), getDependents());
  }

  public Variables variables(Environment environment) {
    return new Variables(environment, variableMap);
  }

  public StepDefinition withVariables(Variables variables) {
    return new StepDefinition(id, executor, variables.state(), plan, getSteps(), getDependents());
  }

  public StepDefinition withVariables(
      Environment environment,
      Function<? super Variables, ? extends Variables> update) {
    return new StepDefinition(
        id,
        executor,
        update.apply(variables(environment)).state(),
        plan,
        getSteps(),
        getDependents());
  }

  public Executor executor() {
    return executor;
  }

  public Stream<Class<?>> resultsObserved() {
    // TODO Auto-generated method stub
    return null;
  }

  public Stream<Class<?>> conditionsPrepared() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj))
      return false;

    StepDefinition that = (StepDefinition) obj;

    return Objects.equals(this.id, that.id) && Objects.equals(this.executor, that.executor)
        && Objects.equals(this.variableMap, that.variableMap)
        && Objects.equals(this.plan, that.plan);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, executor, variableMap, plan, super.hashCode());
  }

  @Override
  StepDefinition with(List<StepDefinition> steps, Map<ExperimentId, StepDefinition> dependents) {
    return new StepDefinition(id, executor, variableMap, plan, steps, dependents);
  }

  public StepDefinition withPlan(ExecutionPlan plan) {
    return new StepDefinition(id, executor, variableMap, plan, getSteps(), getDependents());
  }

  public ExecutionPlan getPlan() {
    return plan;
  }
}
