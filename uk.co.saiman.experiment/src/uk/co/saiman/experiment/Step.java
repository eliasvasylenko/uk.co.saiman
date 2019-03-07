/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.DependencyHandle;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.ConfigurationContext;
import uk.co.saiman.experiment.procedure.Requirement;
import uk.co.saiman.experiment.procedure.ResultRequirement;
import uk.co.saiman.experiment.procedure.Template;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.experiment.product.Result;
import uk.co.saiman.state.StateMap;

/**
 * This class provides a common interface for manipulating, inspecting, and
 * reflecting over the constituent nodes of an experiment. Each node is
 * associated with an implementation of {@link Conductor}.
 * 
 * @author Elias N Vasylenko
 * @param <S> the type of the data describing the experiment configuration
 */
public class Step<S, U extends Product> {
  private final Experiment experiment;
  private final Conductor<S, U> conductor;
  private ExperimentPath path;

  private final S variables;
  private boolean scheduled;

  private Production<?> production;
  private int index;

  private Step(Experiment experiment, Conductor<S, U> conductor, ExperimentPath path) {
    this.experiment = experiment;
    this.conductor = conductor;
    this.path = path;
    this.variables = createVariables();
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

  @SuppressWarnings("unchecked")
  public Instruction getProcedure() {
    return (Instruction) experiment.getInstruction(path);
  }

  /**
   * @return The ID of the node, as configured via {@link ConfigurationContext}. The
   *         ID should be unique amongst the children of a node's parent.
   */
  public String getId() {
    return getProcedure().id();
  }

  @SuppressWarnings("unchecked")
  public Conductor<S, U> getConductor() {
    return conductor;
  }

  public S getVariables() {
    return variables;
  }

  private S createVariables() {
    return getConductor().configureExperiment(new StepProcedureContext());
  }

  void withLock(Runnable action) {
    withLock(() -> {
      action.run();
      return null;
    });
  }

  <T> T withLock(Supplier<T> action) {
    synchronized (experiment) {
      return action.get();
    }
  }

  private void updateProcedure(Function<Instruction, Instruction> modifier) {
    withLock(() -> updateProcedure(modifier.apply(getProcedure())));
  }

  private void updateProcedure(Instruction procedure) {
    getExperiment().updateInstruction(path, procedure);
  }

  /*
   * Experiment Hierarchy
   */

  /**
   * @return the parent part of this experiment, if present, otherwise an empty
   *         optional
   */
  public Optional<Dependency<U>> getDependency() {
    return Optional.empty();// withLock(() ->
                            // Optional.of(parentDependent).map(Dependent::capability));
  }

  public Experiment getExperiment() {
    return experiment;
  }

  public <V, T extends Product> Step<V, T> attach(
      Production<? extends T> production,
      Template<V, T> template) {
    return attach(production, -1, template);
  }

  public <V, T extends Product> Step<V, T> attach(
      Production<? extends T> production,
      int index,
      Template<V, T> template) {
    withLock(() -> {
      updateProcedure(
          getProcedure().with(production, steps -> steps.withInstruction(index, instruction)));
    });
  }

  public void detach() {
    withLock(() -> {

      // TODO

    });
  }

  public ExperimentPath getPath() {
    throw new UnsupportedOperationException();
  }

  public <T extends Product> ProductPath<T> getPath(Production<T> capability) {
    throw new UnsupportedOperationException();
  }

  public Step<?, ?> resolve(ExperimentPath path) {
    throw new UnsupportedOperationException();
  }

  public class StepProcedureContext implements ConfigurationContext {
    @Override
    public String getId() {
      return withLock(() -> getProcedure().id());
    }

    @Override
    public void setId(String id) {
      // TODO Auto-generated method stub

    }

    @Override
    public StateMap getState() {
      return withLock(() -> getProcedure().state());
    }

    @Override
    public void update(StateMap state) {
      // TODO Auto-generated method stub

    }

    @Override
    public <T> DependencyHandle<Result<T>> setRequiredResult(
        Requirement<Result<T>> requirement,
        Dependency<? extends Result<? extends T>> dependency) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public <T> DependencyHandle<Result<T>> addRequiredResult(
        Requirement<Result<T>> requirement,
        Dependency<? extends Result<? extends T>> dependency) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean removeRequiredResult(ResultRequirement<?> requirement, ProductPath dependency) {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean clearRequiredResults(ResultRequirement<?> requirement) {
      // TODO Auto-generated method stub
      return false;
    }
  }
}
