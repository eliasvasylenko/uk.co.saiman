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

import static uk.co.saiman.experiment.ExperimentLifecycleState.COMPLETE;
import static uk.co.saiman.experiment.ExperimentLifecycleState.PREPARATION;
import static uk.co.saiman.experiment.ExperimentLifecycleState.PROCEEDING;
import static uk.co.saiman.experiment.ExperimentLifecycleState.WAITING;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.scheduling.Schedule;
import uk.co.saiman.experiment.scheduling.Scheduler;
import uk.co.saiman.experiment.scheduling.SchedulingContext;
import uk.co.saiman.experiment.scheduling.SchedulingStrategy;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Experiment extends ExperimentStep<ExperimentConfiguration> {
  private final StorageConfiguration<?> storageConfiguration;
  private final Scheduler scheduler;

  private final HotObservable<ExperimentEvent> events = new HotObservable<>();
  private final List<ExperimentEvent> eventQueue = new ArrayList<>();

  public Experiment(
      String id,
      StorageConfiguration<?> storageConfiguration,
      SchedulingStrategy schedulingStrategy) {
    this(id, StateMap.empty(), storageConfiguration, schedulingStrategy);
  }

  public Experiment(
      String id,
      StateMap stateMap,
      StorageConfiguration<?> storageConfiguration,
      SchedulingStrategy schedulingStrategy) {
    this(ExperimentProcedure.instance(), id, stateMap, storageConfiguration, schedulingStrategy);
  }

  protected Experiment(
      ExperimentProcedure procedure,
      String id,
      StateMap stateMap,
      StorageConfiguration<?> storageConfiguration,
      SchedulingStrategy schedulingStrategy) {
    super(procedure, id, stateMap, PREPARATION);
    this.storageConfiguration = storageConfiguration;
    this.scheduler = schedulingStrategy.provideScheduler(getSchedulingContext());
  }

  private SchedulingContext getSchedulingContext() {
    return new SchedulingContext() {
      @Override
      public Experiment experiment() {
        return Experiment.this;
      }

      @Override
      public void commence(ExperimentStep<?> step, Schedule schedule) {
        step.takeStep(schedule);
      }
    };
  }

  public StorageConfiguration<?> getStorageConfiguration() {
    return storageConfiguration;
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  @Override
  public int getIndex() {
    return -1;
  }

  public void dispose() {
    setDetached();
  }

  public Observable<ExperimentEvent> events() {
    return events;
  }

  @Override
  void queueEvents(Collection<? extends ExperimentEvent> events) {
    synchronized (eventQueue) {
      eventQueue.addAll(events);
    }
  }

  void fireEvents() {
    Collection<ExperimentEvent> events = new ArrayList<>();

    synchronized (eventQueue) {
      events.addAll(eventQueue);
      eventQueue.clear();
    }

    events.forEach(this.events::next);
  }

  public synchronized void addSteps(Collection<? extends ExperimentStep<?>> steps) {
    lockExperiment().update(lock -> {
      var allSteps = addTransitiveClosure(steps);
    });
  }

  private Collection<ExperimentStep<?>> addTransitiveClosure(
      Collection<? extends ExperimentStep<?>> steps) {
    List<ExperimentStep<?>> transitiveSteps = new ArrayList<>(steps);
    for (var step : steps) {
      if (step.getLifecycleState() != WAITING && step.getLifecycleState() != PROCEEDING) {
        transitiveSteps.add(step);
      }
    }
    for (int i = 0; i < transitiveSteps.size(); i++) {
      var step = transitiveSteps.get(i);

      if (step.getProcedure().expectations().findAny().isPresent()) {
        step
            .getParent()
            .filter(s -> s.getLifecycleState() != WAITING && s.getLifecycleState() != PROCEEDING)
            .ifPresent(transitiveSteps::add);
      }

      step
          .getInputs()
          .map(Input::getResult)
          .flatMap(Optional::stream)
          .map(Result::getExperimentStep)
          .filter(
              s -> s.getLifecycleState() != COMPLETE
                  && s.getLifecycleState() != WAITING
                  && s.getLifecycleState() != PROCEEDING)
          .forEach(transitiveSteps::add);
    }

    return transitiveSteps;

    /*
     * TODO collect transitive closure of ALL INCOMPLETE experiments which are wired
     * to dependencies as inputs, and ALL ancestors which are linked by way of
     * conditions/expectations. This is our set of requirements.
     * 
     * TODO collect transitive closure of ALL experiments whose dependencies are
     * wired to results via inputs, and ALL descendents which are linked by way of
     * conditions/expectations. This is our set of dependents.
     */

  }

  private void prepareSteps(Collection<? extends ExperimentStep<?>> steps) {
    steps.forEach(this::prepareStep);
    steps.stream().forEach(step -> step.setLifecycleState(WAITING));
  }

  private void takeStep(ExperimentStep<?> step) {

  }

  private void prepareStep(ExperimentStep<?> step) {
    step.clearResults();
  }
}
