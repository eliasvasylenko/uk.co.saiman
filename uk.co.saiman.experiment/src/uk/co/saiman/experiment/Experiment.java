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
import static uk.co.saiman.experiment.path.ExperimentPath.defineAbsolute;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.production.Nothing;
import uk.co.saiman.experiment.production.Results;
import uk.co.saiman.experiment.schedule.Schedule;
import uk.co.saiman.experiment.schedule.Scheduler;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Experiment {
  private ExperimentDefinition definition;
  private NavigableSet<ExperimentPath<Absolute>> enabled = new TreeSet<>();

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

  public Results getResults() {
    return scheduler.getResults();
  }

  public String getId() {
    return getSchedule().getScheduledProcedure().id();
  }

  public void setId(String id) {
    updateProcedure(p -> p.withId(id));
  }

  private synchronized boolean updateProcedure(
      Function<ExperimentDefinition, ExperimentDefinition> modifier) {
    var newProcedure = modifier.apply(definition);
    boolean changed = !definition.equals(newProcedure);
    if (changed) {
      definition = newProcedure;
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

  public synchronized Step attach(StepDefinition<Nothing> step) {
    List.of();
    updateProcedure(p -> p.withStep(step));

    return getStep(ExperimentPath.defineAbsolute().resolve(step.id()));
  }

  public synchronized void close() {

  }

  private Step getStep(ExperimentPath<Absolute> path) {
    var reference = steps.get(path);
    var step = reference == null ? null : reference.get();
    if (step == null) {
      step = new Step(this, definition.findStep(path).get().executor(), path.toAbsolute());
      steps.put(path, new SoftReference<>(step));
    }
    return step;
  }

  void updateInstruction(ExperimentPath<?> path, StepDefinition instruction) {
    // TODO Auto-generated method stub

  }

  public Stream<Step> getIndependentSteps() {
    return definition
        .independentSteps()
        .map(step -> defineAbsolute().resolve(step.id()))
        .map(this::getStep);
  }
}
