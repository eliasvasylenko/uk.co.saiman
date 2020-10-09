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
package uk.co.saiman.experiment.design;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.declaration.ExperimentPath.Relative;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.state.StateMap;

public class ExperimentStepDesign extends ExperimentDesignUnit<Relative, ExperimentStepDesign> {
  private final Executor executor;
  private final StateMap variableMap;
  private final ExecutionPlan plan;
  private final ExperimentId sharedMethodId;

  private ExperimentStepDesign(
      ExperimentId id,
      List<ExperimentStepDesign> steps,
      Map<ExperimentId, ExperimentStepDesign> dependents,
      Executor executor,
      StateMap variableMap,
      ExecutionPlan plan,
      ExperimentId sharedMethodId) {
    super(id, steps, dependents);
    this.executor = executor;
    this.variableMap = variableMap;
    this.plan = plan;
    this.sharedMethodId = sharedMethodId;
  }

  public static ExperimentStepDesign define(ExperimentId id) {
    return new ExperimentStepDesign(
        requireNonNull(id),
        List.of(),
        Map.of(),
        null,
        StateMap.empty(),
        ExecutionPlan.WITHHOLD,
        null);
  }

  public boolean isMethodInstance() {
    return sharedMethodId != null;
  }

  public Optional<ExperimentId> sharedMethodId() {
    return Optional.ofNullable(sharedMethodId);
  }

  public ExperimentStepDesign withSharedMethod(ExperimentId sharedMethodId) {
    return new ExperimentStepDesign(
        id(),
        getSteps(),
        getDependents(),
        executor,
        variableMap,
        plan,
        requireNonNull(sharedMethodId));
  }

  public ExperimentStepDesign withoutSharedMethod() {
    return new ExperimentStepDesign(id(), getSteps(), getDependents(), executor, variableMap, plan, null);
  }

  public ExecutionPlan plan() {
    return plan;
  }

  public ExperimentStepDesign withPlan(ExecutionPlan plan) {
    return new ExperimentStepDesign(
        id(),
        getSteps(),
        getDependents(),
        executor,
        variableMap,
        requireNonNull(plan),
        sharedMethodId);
  }

  public StateMap variableMap() {
    return variableMap;
  }

  public ExperimentStepDesign withVariableMap(StateMap variableMap) {
    return new ExperimentStepDesign(
        id(),
        getSteps(),
        getDependents(),
        executor,
        requireNonNull(variableMap),
        plan,
        sharedMethodId);
  }

  public Variables variables(Environment environment) {
    return new Variables(environment, variableMap);
  }

  public ExperimentStepDesign withVariables(Variables variables) {
    return new ExperimentStepDesign(
        id(),
        getSteps(),
        getDependents(),
        executor,
        variables.state(),
        plan,
        sharedMethodId);
  }

  public ExperimentStepDesign withVariables(
      Environment environment,
      Function<? super Variables, ? extends Variables> update) {
    return new ExperimentStepDesign(
        id(),
        getSteps(),
        getDependents(),
        executor,
        update.apply(variables(environment)).state(),
        plan,
        sharedMethodId);
  }

  public ExperimentStepDesign withExecutor(Executor executor) {
    return new ExperimentStepDesign(
        id(),
        getSteps(),
        getDependents(),
        requireNonNull(executor),
        variableMap,
        plan,
        sharedMethodId);
  }

  public ExperimentStepDesign withoutExecutor() {
    return new ExperimentStepDesign(id(), getSteps(), getDependents(), null, variableMap, plan, sharedMethodId);
  }

  public Optional<Executor> executor() {
    return Optional.ofNullable(executor);
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj))
      return false;

    var that = (ExperimentStepDesign) obj;

    return Objects.equals(this.executor, that.executor) && Objects.equals(this.variableMap, that.variableMap)
        && Objects.equals(this.plan, that.plan);
  }

  @Override
  public int hashCode() {
    return Objects.hash(executor, variableMap, plan, super.hashCode());
  }

  @Override
  ExperimentStepDesign with(
      ExperimentId id,
      List<ExperimentStepDesign> steps,
      Map<ExperimentId, ExperimentStepDesign> dependents) {
    return new ExperimentStepDesign(id, steps, dependents, executor, variableMap, plan, sharedMethodId);
  }

  /**
   * For this experiment step, and each of for its substeps, substitute any
   * {@link #isMethodInstance() method instances} with the shared method of the
   * {@link #sharedMethodId() given id}.
   * 
   * Any variables defined in both the shared method and the instance are
   * overridden by those defined in the instance.
   * 
   * Any substeps defined in both the shared method and the instance are
   * overridden by those defined in the instance. It is an error if any such
   * substeps appear in a different order in the instance to that in which they
   * appear in the shared method.
   * 
   * Substeps are merged in this manner recursively.
   */
  public ExperimentStepDesign substituteSharedMethods(SharedMethods sharedMethods) {
    return substituteSharedMethods(ExperimentPath.toSelf(), sharedMethods);
  }

  ExperimentStepDesign substituteSharedMethods(ExperimentPath<?> containingPath, SharedMethods sharedMethods) {
    var step = sharedMethods.substituteMethod(this, containingPath);
    var path = containingPath.resolve(id());

    return step.withSubsteps(s -> s.map(t -> t.substituteSharedMethods(path, sharedMethods)));
  }

  public Procedure implementInstructions(
      Procedure procedure,
      ExperimentPath<Absolute> containingPath,
      SharedMethods sharedMethods) {
    if (plan() == ExecutionPlan.WITHHOLD) {
      return procedure;
    }

    var step = sharedMethods.substituteMethod(this, containingPath);
    var path = containingPath.resolve(step.id());
    var executor = step.executor().orElseThrow(() -> new MissingExecutorException(path));

    procedure = procedure.withInstruction(path, step.variableMap(), executor);
    return step.implementSubstepInstructions(procedure, path, sharedMethods);
  }
}
