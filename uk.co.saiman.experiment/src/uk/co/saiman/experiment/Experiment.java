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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.event.ExperimentEvent;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.experiment.storage.StorageConfiguration;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

public class Experiment extends ExperimentStep<Void> {
  private static class ExperimentProcedure implements Procedure<Void> {
    @Override
    public Void configureVariables(ExperimentContext<Void> configuration) {
      return null;
    }

    @Override
    public void proceed(ProcedureContext<Void> context) {
      context.prepareCondition(Experiment.SCHEDULED_CONDITION, null);
    }

    @Override
    public Stream<Preparation<?>> preparations() {
      return Stream.of(SCHEDULED_CONDITION);
    }
  }

  private static final ExperimentProcedure PROCEDURE = new ExperimentProcedure();

  private static final String SCHEDULED_CONDITION_ID = Experiment.class.getPackageName()
      + ".submitted";
  private static final Preparation<Void> SCHEDULED_CONDITION = new Preparation<Void>(
      SCHEDULED_CONDITION_ID) {};
  private static final ConditionRequirement<Void> SCHEDULED_REQUIREMENT = new ConditionRequirement<Void>(
      SCHEDULED_CONDITION) {};

  private final Condition<Void> submitted;

  private final StorageConfiguration<?> storageConfiguration;
  private final Deque<ExperimentStep<?>> schedulingQueue = new ArrayDeque<>();

  private final HotObservable<ExperimentEvent> events = new HotObservable<>();
  private final List<ExperimentEvent> eventQueue = new ArrayList<>();

  public Experiment(String id, StorageConfiguration<?> storageConfiguration) {
    super(procedure(), id, StateMap.empty(), PREPARATION);
    this.storageConfiguration = storageConfiguration;
    this.submitted = new Condition<>(this, SCHEDULED_CONDITION);
  }

  @Override
  public void setId(String id) {
    super.setId(id);
  }

  @Override
  public Optional<Experiment> getExperiment() {
    return Optional.of(this);
  }

  @Override
  public Optional<ExperimentStep<?>> getContainer() {
    return Optional.empty();
  }

  public StorageConfiguration<?> getStorageConfiguration() {
    return storageConfiguration;
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
      for (var step : addTransitiveClosure(steps)) {
        new Thread(step::takeStep).run();
      }
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

      step
          .getRequiredConditions()
          .map(Condition::getNode)
          .filter(s -> s.getLifecycleState() != WAITING && s.getLifecycleState() != PROCEEDING)
          .forEach(transitiveSteps::add);

      step
          .getRequiredResults()
          .map(Result::getNode)
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
     * wired to results via inputs, and ALL INCOMPLETE descendents which are linked
     * by way of conditions/expectations. This is our set of dependents.
     * 
     * Consider that dependencies may have their own dependents, and dependents may
     * have other dependencies.
     * 
     * TODO taking the transitive closure of the experiments whose dependencies are
     * wired to results via inputs, if any are already complete or processing, FAIL!
     * They need to be explicitly cancelled/cleared before we can proceed!
     */

  }

  private void prepareStep(ExperimentStep<?> step) {
    step.clearResults();
    step.setLifecycleState(WAITING);
  }

  public static boolean isNameValid(String name) {
    final String ALPHANUMERIC = "[a-zA-Z0-9]+";
    final String DIVIDER_CHARACTERS = "[ \\.\\-_]+";

    return name != null
        && name.matches(ALPHANUMERIC + "(" + DIVIDER_CHARACTERS + ALPHANUMERIC + ")*");
  }

  @Override
  public void schedule() {
    // TODO Auto-generated method stub

  }

  public void attach(ExperimentStep<?> step) {
    submitted.attach(step);
  }

  public static Procedure<Void> procedure() {
    return PROCEDURE;
  }

  public static ConditionRequirement<Void> scheduledRequirement() {
    return SCHEDULED_REQUIREMENT;
  }

  public static Preparation<Void> scheduledCondition() {
    return SCHEDULED_CONDITION;
  }
}
