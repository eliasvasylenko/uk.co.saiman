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
import static uk.co.saiman.experiment.design.ExecutionPlan.EXECUTE;
import static uk.co.saiman.experiment.design.ExecutionPlan.WITHHOLD;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.design.ExperimentDesignException;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.problem.DesignProblem;
import uk.co.saiman.experiment.problem.ExperimentProblem;
import uk.co.saiman.experiment.problem.ProcedureProblem;
import uk.co.saiman.experiment.problem.UnknownProblem;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.procedure.ProcedureException;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * This class provides a common interface for manipulating, inspecting, and
 * reflecting over the constituent nodes of an experiment. Each node is
 * associated with an implementation of {@link Executor}.
 * 
 * @author Elias N Vasylenko
 */
public class Step {
  private final Experiment experiment;
  private final ExperimentPath<Absolute> path;
  private boolean detached;

  private final Log log;

  private ExperimentStepDesign design;
  private ExperimentStepDesign concreteDesign;
  private Instruction instruction;
  private final Set<ExperimentProblem> problems;

  Step(Experiment experiment, ExperimentPath<Absolute> path, Log log) {
    this.log = log.mapMessage(message -> "At step '" + path + "': " + message);

    this.experiment = experiment;
    this.path = path;

    this.problems = new HashSet<>();
  }

  Procedure update(Procedure cumulativeProcedure) {
    this.problems.clear();
    this.design = experiment.getStepDesign(path).orElseGet(() -> ExperimentStepDesign.define(getId()));
    this.concreteDesign = design;

    try {
      var sharedMethods = experiment.getDesign().sharedMethods();

      this.concreteDesign = getSuperstep()
          .map(
              parent -> getConcreteDesign()
                  .materializeSubstep(sharedMethods, parent.getPath().parent().get(), getId())
                  .get())
          .orElseGet(() -> experiment.getDesign().materializeSubstep(getId()).get());
      cumulativeProcedure = concreteDesign
          .implementInstruction(cumulativeProcedure, path.parent().get(), experiment.getDesign().sharedMethods());
      this.instruction = cumulativeProcedure.instruction(path).get();

      /**
       * TODO put some more useful error markers here. There is a lot of potential!
       * The exceptions for design and instruction errors return useful path info and
       * are typed to describe the nature of the problem.
       */
    } catch (ExperimentDesignException e) {
      this.problems.add(new DesignProblem());

    } catch (ProcedureException e) {
      this.problems.add(new ProcedureProblem());

    } catch (Exception e) {
      log.log(Level.WARN, "Unknown update error", e);
      this.problems.add(new UnknownProblem());
    }

    return getSubsteps().reduce(cumulativeProcedure, (p, s) -> s.update(p), StreamUtilities.throwingMerger());
  }

  public Stream<ExperimentProblem> getProblems() {
    return problems.stream();
  }

  /*
   * Environment
   */

  private Environment getEnvironment() {
    return experiment.getEnvironment();
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

  public ExperimentStepDesign getDesign() {
    return lock(() -> design);
  }

  public ExperimentStepDesign getConcreteDesign() {
    return lock(() -> concreteDesign);
  }

  public ExperimentId getId() {
    return path.id(path.depth() - 1);
  }

  public void setId(ExperimentId id) {
    lock(() -> {
      experiment.moveStep(this, id);
    });
  }

  public Optional<Executor> getExecutor() {
    return getDesign().executor();
  }

  public Optional<Executor> getConcreteExecutor() {
    return getConcreteDesign().executor();
  }

  public Variables getVariables() {
    return lock(() -> getDesign().variables(getEnvironment()));
  }

  public Variables getConcreteVariables() {
    return lock(() -> getConcreteDesign().variables(getEnvironment()));
  }

  public <T> void setVariable(Variable<T> variable, T value) {
    updateVariable(variable, previous -> Optional.ofNullable(value));
  }

  public <T> void updateVariable(Variable<T> variable, Function<? super Optional<T>, ? extends Optional<T>> value) {
    lock(() -> {
      experiment.updateStep(this, variable, value);
    });
  }

  /*
   * Experiment Hierarchy
   */

  public Optional<Step> getSuperstep() {
    return lock(
        () -> path.parent().filter(path -> !path.isEmpty()).flatMap(parentPath -> getExperiment().getStep(parentPath)));
  }

  public Optional<Step> getSubstep(ExperimentId id) {
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

  public Step attach(ExperimentStepDesign template) {
    return attach(-1, template);
  }

  public Step attach(int index, ExperimentStepDesign step) {
    return lock(() -> {
      return experiment.addStep(this, index, step);
    });
  }

  public void detach() {
    lock(() -> {
      detached = true;
      experiment.removeStep(this);
    });
  }

  public boolean isDetached() {
    return detached;
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

  public Stream<Step> getSubsteps() {
    return lock(() -> {
      return getDesign()
          .substeps()
          .map(step -> path.resolve(step.id()))
          .map(experiment::getStep)
          .flatMap(Optional::stream)
          .collect(toList()); // call the terminal op while we're still in the lock
    }).stream();
  }

  public Stream<VariableDeclaration<?>> getVariableDeclarations() {
    return getInstruction().variableDeclarations();
  }

  public Stream<? extends Result<?>> getResults() {
    return lock(() -> {
      var output = experiment.getOutput();
      return output.resultPaths(path).map(output::resolveResult).collect(toList());
      // call the stream's terminal op while we're still in the lock
    }).stream();
  }

  public Instruction getInstruction() {
    return instruction;
  }

  public int getIndex() {
    return (int) getSuperstep()
        .map(Step::getSubsteps)
        .orElseGet(experiment::getSubsteps)
        .takeWhile(s -> s != this)
        .count();
  }
}
