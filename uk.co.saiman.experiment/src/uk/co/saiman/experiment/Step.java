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
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.procedure.Template;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.Variables;

/**
 * This class provides a common interface for manipulating, inspecting, and
 * reflecting over the constituent nodes of an experiment. Each node is
 * associated with an implementation of {@link Conductor}.
 * 
 * @author Elias N Vasylenko
 */
public class Step {
  private final Experiment experiment;
  private final Conductor<?> conductor;
  private ExperimentPath<Absolute> path;

  private boolean scheduled;

  private Production<?> production;
  private int index;

  private Step(Experiment experiment, Conductor<?> conductor, ExperimentPath<Absolute> path) {
    this.experiment = experiment;
    this.conductor = conductor;
    this.path = path;
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

  public Instruction getProcedure() {
    return (Instruction) experiment.getInstruction(path);
  }

  public String getId() {
    return getProcedure().id();
  }

  public Conductor<?> getConductor() {
    return conductor;
  }

  public <T> T getVariable(Variable<T> variable) {
    // TODO Auto-generated method stub
    return null;
  }

  public <T> void setVariable(Variable<T> variable, Function<? super T, ? extends T> value) {
    // TODO Auto-generated method stub
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
  public Optional<Dependency<?, Absolute>> getDependency() {
    return Optional.empty();// withLock(() ->
                            // Optional.of(parentDependent).map(Dependent::capability));
  }

  public Experiment getExperiment() {
    return experiment;
  }

  public <T extends Product> Step attach(Production<? extends T> production, Template<T> template) {
    return attach(production, -1, template);
  }

  public <V, T extends Product> Step attach(
      Production<? extends T> production,
      int index,
      Template<T> template) {
    return withLock(() -> {
      return null;
    });
  }

  public void detach() {
    withLock(() -> {

      // TODO

    });
  }

  public ExperimentPath<Absolute> getPath() {
    throw new UnsupportedOperationException();
  }

  public <T extends Product> Dependency<T, Absolute> getPath(Production<T> capability) {
    throw new UnsupportedOperationException();
  }

  public Step resolve(ExperimentPath<Absolute> path) {
    throw new UnsupportedOperationException();
  }

  public Instruction getInstruction() {
    // TODO Auto-generated method stub
    return null;
  }

  public Stream<? extends Step> getDependentSteps() {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isDetached() {
    // TODO Auto-generated method stub
    return false;
  }

  public Variables getVariables() {
    // TODO Auto-generated method stub
    return null;
  }
}
