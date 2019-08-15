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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.event.AddStepEvent;
import uk.co.saiman.experiment.event.ChangeVariableEvent;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext.NoOpPlanningContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.requirement.ProductPath;
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
  private boolean scheduled;

  Step(Experiment experiment, Executor conductor, ExperimentPath<Absolute> path) {
    this.experiment = experiment;
    this.executor = conductor;
    this.path = path;
  }

  /*
   * Environment
   */

  private GlobalEnvironment getStaticEnvironment() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Add the step to the schedule. This doesn't necessarily being processing,
   * though if processing is already underway this will schedule the step as part
   * of the ongoing process.
   */
  public void schedule() {
    // TODO lock and set parents as scheduled
    scheduled = true;
  }

  /**
   * Remove the step from the schedule, canceling ongoing procedures or deleting
   * observed results where appropriate.
   */
  public void unschedule() {
    // TODO lock and set parents as scheduled
    scheduled = false;
  }

  public boolean isScheduled() {
    return scheduled;
  }

  public StepDefinition getDefinition() {
    synchronized (experiment) {
      return experiment.getStepDefinition(path).get();
    }
  }

  public ExperimentId getId() {
    return getDefinition().id();
  }

  public Executor getExecutor() {
    return executor;
  }

  public <T> Optional<T> getVariable(Variable<T> variable) {
    return getVariables().get(variable);
  }

  public <T> void setVariable(
      Variable<T> variable,
      Function<? super Optional<T>, ? extends T> value) {
    synchronized (experiment) {
      if (experiment
          .updateStepDefinition(
              path,
              getDefinition()
                  .withVariables(
                      getStaticEnvironment(),
                      variables -> variables.with(variable, value)))) {
        experiment.fireEvent(new ChangeVariableEvent(this, variable));
      }
    }
  }

  /*
   * Experiment Hierarchy
   */

  public Optional<Step> getDependencyStep() {
    synchronized (experiment) {
      return path
          .parent()
          .filter(path -> !path.isEmpty())
          .map(parentPath -> getExperiment().getStep(parentPath));
    }
  }

  public synchronized Step getDependentStep(ExperimentId id) {
    synchronized (experiment) {
      return experiment.getStep(path.resolve(id));
    }
  }

  public Experiment getExperiment() {
    return experiment;
  }

  public Step attach(StepDefinition template) {
    return attach(-1, template);
  }

  public Step attach(int index, StepDefinition stepDefinition) {
    synchronized (experiment) {
      boolean changed = experiment
          .updateStepDefinition(
              path,
              index < 0
                  ? getDefinition().withSubstep(stepDefinition)
                  : getDefinition().withSubstep(index, stepDefinition));

      Step newStep = getDependentStep(stepDefinition.id());
      if (changed) {
        experiment.fireEvent(new AddStepEvent(newStep));
      }
      return newStep;
    }
  }

  public void detach() {
    synchronized (experiment) {

      // TODO

    }
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

  public Stream<? extends Step> getDependentSteps() {
    synchronized (experiment) {
      return getDefinition()
          .substeps()
          .map(step -> path.resolve(step.id()))
          .map(experiment::getStep);
    }
  }

  public boolean isDetached() {
    return false;
  }

  public Variables getVariables() {
    synchronized (experiment) {
      return getDefinition().variables(getStaticEnvironment());
    }
  }

  public Stream<VariableDeclaration<?>> getVariableDeclarations() {
    List<VariableDeclaration<?>> declarations = new ArrayList<>();

    var variables = getVariables();
    getExecutor().plan(new NoOpPlanningContext() {
      @Override
      public <T> Optional<T> declareVariable(VariableDeclaration<T> declaration) {
        declareVariable(declaration);
        return variables.get(declaration.variable());
      }
    });

    return declarations.stream();
  }

  public Stream<? extends Result<?>> getResults() {
    synchronized (experiment) {
      var output = experiment.getResults();
      return output.resultPaths(path).map(output::resolveResult);
    }
  }

  public Instruction getInstruction() {
    return new Instruction(path, getDefinition().variableMap(), getExecutor());
  }
}
