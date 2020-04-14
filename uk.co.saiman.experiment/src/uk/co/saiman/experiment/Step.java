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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.definition.ExecutionPlan.EXECUTE;
import static uk.co.saiman.experiment.definition.ExecutionPlan.WITHHOLD;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.experiment.variables.Variables;

/**
 * This class provides a common interface for manipulating, inspecting, and
 * reflecting over the constituent nodes of an experiment. Each node is
 * associated with an implementation of {@link Executor}.
 * 
 * @author Elias N Vasylenko
 */
public class Step {
  private final Experiment experiment;
  private final Executor executor;
  private ExperimentPath<Absolute> path;
  private boolean detached;

  Step(Experiment experiment, Executor conductor, ExperimentPath<Absolute> path) {
    this.experiment = experiment;
    this.executor = conductor;
    this.path = path;
  }

  /*
   * Environment
   */

  private Environment getGlobalEnvironment() {
    return experiment.getGlobalEnvironment();
  }

  /**
   * Add the step to the schedule. All dependent steps, and all dependency steps,
   * are also added to the schedule.
   */
  public void schedule() {
    lock(() -> {
      experiment.planStep(this, EXECUTE);
    });
  }

  /**
   * Remove the step from the schedule. All dependent steps, and all dependency
   * steps are also removed from the schedule, except for those dependencies which
   * are also required by other, unrelated steps.
   */
  public void unschedule() {
    lock(() -> {
      experiment.planStep(this, WITHHOLD);
    });
  }

  public StepDefinition getDefinition() {
    return lock(() -> experiment.getStepDefinition(path).get());
  }

  public ExperimentId getId() {
    return path.id(path.depth() - 1);
  }

  public void setId(ExperimentId id) {
    lock(() -> {
      experiment.moveStep(this, id);
    });
  }

  public Executor getExecutor() {
    return executor;
  }

  public Variables getVariables() {
    return lock(() -> getDefinition().variables(getGlobalEnvironment()));
  }

  public <T> Optional<T> getVariable(Variable<T> variable) {
    return getVariables().get(variable);
  }

  public <T> void setVariable(Variable<T> variable, T value) {
    updateVariable(variable, previous -> Optional.ofNullable(value));
  }

  public <T> void updateVariable(
      Variable<T> variable,
      Function<? super Optional<T>, ? extends Optional<T>> value) {
    lock(() -> {
      experiment.updateStep(this, variable, value);
    });
  }

  /*
   * Experiment Hierarchy
   */

  public Optional<Step> getDependencyStep() {
    return lock(
        () -> path
            .parent()
            .filter(path -> !path.isEmpty())
            .flatMap(parentPath -> getExperiment().getStep(parentPath)));
  }

  public Optional<Step> getDependentStep(ExperimentId id) {
    return lock(() -> experiment.getStep(path.resolve(id)));
  }

  public Experiment getExperiment() {
    return experiment;
  }

  protected void lock(Runnable action) {
    lock(() -> {
      action.run();
      return null;
    });
  }

  protected <T> T lock(Supplier<T> action) {
    assertAttached();
    synchronized (experiment) {
      assertAttached();
      return action.get();
    }
  }

  private void assertAttached() {
    if (detached) {
      throw new ExperimentException("Experiment step is detached from experiment " + path);
    }
  }

  public Step attach(StepDefinition template) {
    return attach(-1, template);
  }

  public Step attach(int index, StepDefinition stepDefinition) {
    return lock(() -> {
      return experiment.addStep(this, index, stepDefinition);
    });
  }

  public void detach() {
    lock(() -> {
      detached = true;
      experiment.removeStep(this);
    });
  }

  public ExperimentPath<Absolute> getPath() {
    return path;
  }

  public <T> ProductPath<Absolute, Result<T>> getResultPath(Class<T> type) {
    return ProductPath.toResult(path, type);
  }

  public <T> ProductPath<Absolute, Condition<T>> getConditionPath(Class<T> type) {
    return ProductPath.toCondition(path, type);
  }

  public Step resolve(ExperimentPath<Absolute> path) {
    throw new UnsupportedOperationException();
  }

  public Stream<Step> getDependentSteps() {
    return lock(() -> {
      return getDefinition()
          .substeps()
          .map(step -> path.resolve(step.id()))
          .map(experiment::getStep)
          .flatMap(Optional::stream)
          .collect(toList()); // call the terminal op while we're still in the lock
    }).stream();
  }

  public boolean isDetached() {
    return false;
  }

  public Stream<Class<?>> getObservations() {
    return Procedures.getObservations(getInstruction(), getGlobalEnvironment());
  }

  public boolean prepares(Class<?> type) {
    return getPreparations().anyMatch(type::equals);
  }

  public boolean observes(Class<?> type) {
    return getObservations().anyMatch(type::equals);
  }

  public Stream<Class<?>> getPreparations() {
    return Procedures.getPreparations(getInstruction(), getGlobalEnvironment());
  }

  public Stream<VariableDeclaration<?>> getVariableDeclarations() {
    return Procedures.getVariableDeclarations(getInstruction(), getGlobalEnvironment());
  }

  public Stream<? extends Result<?>> getResults() {
    return lock(() -> {
      var output = experiment.getOutput();
      return output.resultPaths(path).map(output::resolveResult).collect(toList());
      // call the terminal op while we're still in the lock
    }).stream();
  }

  public Instruction getInstruction() {
    return new Instruction(path, getDefinition().variableMap(), getExecutor());
  }

  public int getIndex() {
    return (int) getDependencyStep()
        .map(Step::getDependentSteps)
        .orElseGet(experiment::getIndependentSteps)
        .takeWhile(s -> s != this)
        .count();
  }
}
