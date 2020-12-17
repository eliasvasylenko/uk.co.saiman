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

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.experiment.design.ExecutionPlan.EXECUTE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.design.ExecutionPlan;
import uk.co.saiman.experiment.design.ExperimentDesign;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.event.AddStepEvent;
import uk.co.saiman.experiment.event.ChangeVariableEvent;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.event.MoveStepEvent;
import uk.co.saiman.experiment.event.PlanStepEvent;
import uk.co.saiman.experiment.event.RemoveStepEvent;
import uk.co.saiman.experiment.event.RenameExperimentEvent;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.schedule.Schedule;
import uk.co.saiman.experiment.schedule.Scheduler;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * A mutable model of a complex, hierarchical experiment. An experiment is
 * backed by a reference to an immutable {@link ExperimentDesign design object},
 * which provides a well-defined snapshot of the experiment state at any given
 * moment, even in the presence of concurrent modification by multiple threads.
 * This design is used to implement a {@link Procedure procedure} for the
 * conducting of the experiment.
 * 
 * @author Elias N Vasylenko
 */
public class Experiment {
  private ExperimentDesign design;

  private final Scheduler scheduler;

  private Map<ExperimentPath<Absolute>, Step> steps = new HashMap<>();

  private final HotObservable<ExperimentEvent> events = new HotObservable<>();

  private final Supplier<Environment> environmentSupplier;
  private Environment environment;

  private final Log log;

  public Experiment(
      ExperimentDesign procedure,
      StorageConfiguration<?> storageConfiguration,
      ExecutorService executorService,
      Supplier<Environment> environment,
      Log log) {
    this.log = log.mapMessage(message -> "In experiment '" + design.id() + "': " + message);

    this.environmentSupplier = environment;
    this.environment = environmentSupplier.get();
    this.design = null;
    this.scheduler = new Scheduler(storageConfiguration, executorService, this.log);

    updateDesign(requireNonNull(procedure));
  }

  public ExecutorService getExecutorService() {
    return scheduler.getExecutorService();
  }

  public Environment getEnvironment() {
    return environment;
  }

  public void refreshEnvironment() {
    var environment = environmentSupplier.get();
    if (!environment.equals(this.environment)) {
      this.environment = environment;
      update();
    }
  }

  public ExperimentDesign getDesign() {
    return design;
  }

  public synchronized void scheduleAll() {
    updateDesign(getDesign().withSubsteps(steps -> steps.map(Experiment::withScheduled)));
  }

  public synchronized Output conduct() {
    return scheduler.getSchedule().map(Schedule::conduct).orElseGet(() -> scheduler.getOutput());
  }

  static ExperimentStepDesign withScheduled(ExperimentStepDesign step) {
    return step.withPlan(EXECUTE).withSubsteps(steps -> steps.map(Experiment::withScheduled));
  }

  public Output getOutput() {
    return scheduler.getOutput();
  }

  public ExperimentId getId() {
    return getDesign().id();
  }

  public synchronized void setId(ExperimentId id) {
    ExperimentId previousId = getId();

    if (updateDesign(getDesign().withId(id))) {
      fireEvent(new RenameExperimentEvent(this, previousId));
    }
  }

  private synchronized boolean updateDesign(ExperimentDesign design) {
    boolean changed = !design.equals(this.design);
    if (changed) {
      this.design = design;
      update();
    }
    return changed;
  }

  private synchronized void update() {
    var cumulativeProcedure = Procedure.empty(getId(), getEnvironment());
    getSubsteps().reduce(cumulativeProcedure, (p, s) -> s.update(p), StreamUtilities.throwingMerger());

    try {
      var completeProcedure = design.implementProcedure(getEnvironment());
      scheduler.scheduleProcedure(completeProcedure);
    } catch (Exception e) {
      log.log(Level.WARN, "Failed to schedule experiment '" + getId() + "'", e);
    }
  }

  public StorageConfiguration<?> getStorageConfiguration() {
    return scheduler.getStorageConfiguration();
  }

  public Observable<ExperimentEvent> events() {
    return events;
  }

  public synchronized Step attach(ExperimentStepDesign stepDesign) {
    if (getDesign().findSubstep(stepDesign.id()).isPresent()) {
      throw new ExperimentException("Experiment step already exists with id " + stepDesign.id());
    }

    boolean changed = updateDesign(getDesign().withSubstep(stepDesign));

    Step newStep = getSubstep(stepDesign.id()).get();
    if (changed) {
      fireEvent(new AddStepEvent(newStep));
    }
    return newStep;
  }

  public synchronized void close() {

  }

  public synchronized Optional<Step> getStep(ExperimentPath<Absolute> path) {
    return Optional.ofNullable(steps.get(path));
  }

  Optional<ExperimentStepDesign> getStepDesign(ExperimentPath<Absolute> path) {
    return getDesign().findSubstep(path);
  }

  private synchronized boolean updateStepDesign(ExperimentPath<Absolute> path, ExperimentStepDesign stepDesign) {
    return getDesign().withSubstep(path, s -> Optional.ofNullable(stepDesign)).map(d -> updateDesign(d)).orElse(false);
  }

  synchronized Step addStep(Step parent, int index, ExperimentStepDesign stepDesign) {
    var parentDesign = parent.getDesign();
    if (parentDesign.findSubstep(stepDesign.id()).isPresent()) {
      throw new ExperimentException("Experiment step already exists with id " + stepDesign.id());
    }

    var path = parent.getPath();

    updateStepDesign(
        path,
        index < 0 ? parentDesign.withSubstep(stepDesign) : parentDesign.withSubstep(index, stepDesign));

    var step = parent.getSubstep(stepDesign.id()).get();

    fireEvent(new AddStepEvent(step));

    return step;
  }

  synchronized void removeStep(Step step) {
    var path = step.getPath();

    if (updateStepDesign(path, null)) {
      fireEvent(new RemoveStepEvent(step, path.parent().flatMap(this::getStep)));
    }
  }

  synchronized void moveStep(Step step, ExperimentId id) {
    var previousId = step.getId();
    if (previousId.equals(id)) {
      return;
    }

    var path = step.getPath();
    var parentDesign = path.parent().flatMap(this::getStepDesign).get();
    if (parentDesign.findSubstep(id).isPresent()) {
      throw new ExperimentException("Experiment step already exists with id " + id);
    }

    for (var stepPath : List.copyOf(steps.keySet())) {
      var relativePath = stepPath.relativeTo(path);
      if (relativePath.ancestorDepth() == 0) {
        var newPath = path.parent().get().resolve(id).resolve(relativePath).get();
        steps.put(newPath, steps.remove(stepPath));
      }
    }

    if (updateStepDesign(path, getStepDesign(path).get().withId(id))) {
      fireEvent(new MoveStepEvent(step, step.getSuperstep(), path.id(path.depth() - 1)));
    }
  }

  synchronized <T> void updateStep(
      Step step,
      Variable<T> variable,
      Function<? super Optional<T>, ? extends Optional<T>> value) {
    var path = step.getPath();
    if (updateStepDesign(
        path,
        getStepDesign(path).get().withVariables(getEnvironment(), variables -> variables.withOpt(variable, value)))) {
      fireEvent(new ChangeVariableEvent(step, variable));
    }
  }

  synchronized <T> void planStep(Step step, ExecutionPlan plan) {
    var path = step.getPath();
    if (updateStepDesign(path, getStepDesign(path).get().withPlan(plan))) {
      fireEvent(new PlanStepEvent(step, plan));
    }
  }

  private void fireEvent(ExperimentEvent event) {
    events.next(event);
  }

  public synchronized Optional<Step> getSubstep(ExperimentId id) {
    return getStep(ExperimentPath.toRoot().resolve(id));
  }

  public synchronized Stream<Step> getSubsteps() {
    return getDesign()
        .substeps()
        .map(step -> ExperimentPath.toRoot().resolve(step.id()))
        .map(this::getStep)
        .flatMap(Optional::stream);
  }
}
