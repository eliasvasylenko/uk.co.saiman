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
import static uk.co.saiman.experiment.graph.ExperimentPath.defineAbsolute;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.dependency.Nothing;
import uk.co.saiman.experiment.event.AddStepEvent;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.event.RenameExperimentEvent;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.graph.ExperimentPath;
import uk.co.saiman.experiment.graph.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.event.ConductorEvent;
import uk.co.saiman.experiment.production.Output;
import uk.co.saiman.experiment.schedule.Schedule;
import uk.co.saiman.experiment.schedule.Scheduler;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Experiment {
  private ExperimentDefinition definition;

  private final Scheduler scheduler;

  private Map<ExperimentPath<Absolute>, Reference<Step>> steps = new HashMap<>();

  private final HotObservable<ExperimentEvent> events = new HotObservable<>();

  public Experiment(ExperimentDefinition procedure, StorageConfiguration<?> storageConfiguration) {
    this.definition = requireNonNull(procedure);
    this.scheduler = new Scheduler(storageConfiguration);
  }

  public ExperimentDefinition getDefinition() {
    return definition;
  }

  public Schedule getSchedule() {
    return scheduler.getSchedule().get();
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
    boolean changed = !this.definition.equals(definition);
    if (changed) {
      this.definition = definition;
      scheduler.schedule(definition.procedure());
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

  public synchronized Step attach(StepDefinition<Nothing> stepDefinition) {
    boolean changed = updateDefinition(getDefinition().withSubstep(stepDefinition));

    Step newStep = getIndependentStep(stepDefinition.id());
    if (changed) {
      fireEvent(new AddStepEvent(newStep));
    }
    return newStep;
  }

  public synchronized void close() {

  }

  public Step getStep(ExperimentPath<Absolute> path) {
    var reference = steps.get(path);
    var step = reference == null ? null : reference.get();
    if (step == null) {
      step = new Step(this, definition.findSubstep(path).get().executor(), path.toAbsolute());
      steps.put(path, new SoftReference<>(step));
    }
    return step;
  }

  Optional<StepDefinition<?>> getStepDefinition(ExperimentPath<Absolute> path) {
    return getDefinition().findSubstep(path);
  }

  synchronized boolean updateStepDefinition(
      ExperimentPath<Absolute> path,
      StepDefinition<?> stepDefinition) {
    return getDefinition()
        .withSubstep(path, s -> Optional.ofNullable(stepDefinition))
        .map(d -> updateDefinition(d))
        .orElse(false);
  }

  void fireEvent(ExperimentEvent event) {
    events.next(event);
  }

  public synchronized Step getIndependentStep(ExperimentId id) {
    return getStep(ExperimentPath.defineAbsolute().resolve(id));
  }

  public synchronized Stream<Step> getIndependentSteps() {
    return definition
        .independentSteps()
        .map(step -> defineAbsolute().resolve(step.id()))
        .map(this::getStep);
  }
}
