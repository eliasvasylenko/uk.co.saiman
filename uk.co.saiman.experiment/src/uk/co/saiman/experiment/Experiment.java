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

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.experiment.declaration.ExperimentPath.toRoot;
import static uk.co.saiman.experiment.definition.ExecutionPlan.EXECUTE;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.definition.ExecutionPlan;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.event.AddStepEvent;
import uk.co.saiman.experiment.event.ChangeVariableEvent;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.event.MoveStepEvent;
import uk.co.saiman.experiment.event.PlanStepEvent;
import uk.co.saiman.experiment.event.RemoveStepEvent;
import uk.co.saiman.experiment.event.RenameExperimentEvent;
import uk.co.saiman.experiment.output.Output;
import uk.co.saiman.experiment.procedure.event.ConductorEvent;
import uk.co.saiman.experiment.schedule.Scheduler;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Experiment {
  private ExperimentDefinition definition;

  private final Scheduler scheduler;

  private Map<ExperimentPath<Absolute>, Reference<Step>> steps = new HashMap<>();

  private final HotObservable<ExperimentEvent> events = new HotObservable<>();

  private final Supplier<GlobalEnvironment> globalEnvironment;

  public Experiment(
      ExperimentDefinition procedure,
      StorageConfiguration<?> storageConfiguration,
      Supplier<GlobalEnvironment> globalEnvironment) {
    this.globalEnvironment = globalEnvironment;
    this.definition = null;
    this.scheduler = new Scheduler(storageConfiguration);

    updateDefinition(requireNonNull(procedure));
  }

  GlobalEnvironment getGlobalEnvironment() {
    return globalEnvironment.get();
  }

  public ExperimentDefinition getDefinition() {
    return definition;
  }

  public synchronized void scheduleAll() {
    updateDefinition(definition.withSubsteps(steps -> steps.map(Experiment::withPlan)));
  }

  public synchronized void conduct() {
    scheduler.getSchedule().get().conduct();
  }

  static StepDefinition withPlan(StepDefinition step) {
    return step.withPlan(EXECUTE).withSubsteps(steps -> steps.map(Experiment::withPlan));
  }

  public Output getResults() {
    return scheduler.getResults();
  }

  public ExperimentId getId() {
    return getDefinition().id();
  }

  public synchronized void setId(ExperimentId id) {
    ExperimentId previousId = getId();

    if (updateDefinition(getDefinition().withId(id))) {
      fireEvent(new RenameExperimentEvent(this, previousId));
    }
  }

  private synchronized boolean updateDefinition(ExperimentDefinition definition) {
    boolean changed = !definition.equals(this.definition);
    if (changed) {
      this.definition = definition;
      scheduler.schedule(definition.procedure(getGlobalEnvironment()));
    }
    return changed;
  }

  public StorageConfiguration<?> getStorageConfiguration() {
    return scheduler.getStorageConfiguration();
  }

  public Observable<ExperimentEvent> events() {
    return events;
  }

  public Observable<ConductorEvent> conductorEvents() {
    return scheduler.conductorEvents();
  }

  public synchronized Step attach(StepDefinition stepDefinition) {
    if (getDefinition().findSubstep(stepDefinition.id()).isPresent()) {
      throw new ExperimentException(
          "Experiment step already exists with id " + stepDefinition.id());
    }

    boolean changed = updateDefinition(getDefinition().withSubstep(stepDefinition));

    Step newStep = getIndependentStep(stepDefinition.id()).get();
    if (changed) {
      fireEvent(new AddStepEvent(newStep));
    }
    return newStep;
  }

  public synchronized void close() {

  }

  public Optional<Step> getStep(ExperimentPath<Absolute> path) {
    var reference = steps.get(path);

    if (reference != null) {
      var step = reference.get();
      if (step != null) {
        return Optional.of(step);
      }
    }

    return definition
        .findSubstep(path)
        .map(step -> new Step(this, step.executor(), path.toAbsolute()))
        .map(step -> {
          steps.put(path, new SoftReference<>(step));
          return step;
        });
  }

  Optional<StepDefinition> getStepDefinition(ExperimentPath<Absolute> path) {
    return getDefinition().findSubstep(path);
  }

  private synchronized boolean updateStepDefinition(
      ExperimentPath<Absolute> path,
      StepDefinition stepDefinition) {
    return getDefinition()
        .withSubstep(path, s -> Optional.ofNullable(stepDefinition))
        .map(d -> updateDefinition(d))
        .orElse(false);
  }

  synchronized Step addStep(Step parent, int index, StepDefinition stepDefinition) {
    var parentDefinition = parent.getDefinition();
    if (parentDefinition.findSubstep(stepDefinition.id()).isPresent()) {
      throw new ExperimentException(
          "Experiment step already exists with id " + stepDefinition.id());
    }

    var path = parent.getPath();

    updateStepDefinition(
        path,
        index < 0
            ? parentDefinition.withSubstep(stepDefinition)
            : parentDefinition.withSubstep(index, stepDefinition));

    var step = parent.getDependentStep(stepDefinition.id()).get();

    fireEvent(new AddStepEvent(step));

    return step;
  }

  synchronized void removeStep(Step step) {
    var path = step.getPath();

    for (var stepPath : List.copyOf(steps.keySet())) {
      if (stepPath.relativeTo(path).ancestorDepth() == 0) {
        steps.remove(stepPath);
      }
    }

    /*
     * TODO remove all children in map
     */
    if (updateStepDefinition(path, null)) {
      fireEvent(new RemoveStepEvent(step, path.parent().flatMap(this::getStep)));
    }
  }

  synchronized void moveStep(Step step, ExperimentId id) {
    var previousId = step.getId();
    if (previousId.equals(id)) {
      return;
    }

    var path = step.getPath();
    var parentDefinition = path.parent().flatMap(this::getStepDefinition).get();
    if (parentDefinition.findSubstep(id).isPresent()) {
      throw new ExperimentException("Experiment step already exists with id " + id);
    }

    for (var stepPath : List.copyOf(steps.keySet())) {
      var relativePath = stepPath.relativeTo(path);
      if (relativePath.ancestorDepth() == 0) {
        var newPath = path.parent().get().resolve(id).resolve(relativePath).get();
        steps.put(newPath, steps.remove(stepPath));
      }
    }

    if (updateStepDefinition(path, getStepDefinition(path).get().withId(id))) {
      fireEvent(new MoveStepEvent(step, step.getDependencyStep(), path.id(path.depth() - 1)));
    }
  }

  synchronized <T> void updateStep(
      Step step,
      Variable<T> variable,
      Function<? super Optional<T>, ? extends Optional<T>> value) {
    var path = step.getPath();
    if (updateStepDefinition(
        path,
        getStepDefinition(path)
            .get()
            .withVariables(
                getGlobalEnvironment(),
                variables -> variables.withOpt(variable, value)))) {
      fireEvent(new ChangeVariableEvent(step, variable));
    }
  }

  synchronized <T> void planStep(Step step, ExecutionPlan plan) {
    var path = step.getPath();
    if (updateStepDefinition(path, getStepDefinition(path).get().withPlan(plan))) {
      fireEvent(new PlanStepEvent(step, plan));
    }
  }

  private void fireEvent(ExperimentEvent event) {
    events.next(event);
  }

  public synchronized Optional<Step> getIndependentStep(ExperimentId id) {
    return getStep(ExperimentPath.toRoot().resolve(id));
  }

  public synchronized Stream<Step> getIndependentSteps() {
    return definition
        .substeps()
        .map(step -> toRoot().resolve(step.id()))
        .map(this::getStep)
        .flatMap(Optional::stream);
  }
}
