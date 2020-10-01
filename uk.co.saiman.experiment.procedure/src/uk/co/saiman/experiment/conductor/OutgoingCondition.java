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
 * This file is part of uk.co.saiman.experiment.conductor.
 *
 * uk.co.saiman.experiment.conductor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.conductor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.conductor;

import static uk.co.saiman.experiment.executor.Evaluation.ORDERED;
import static uk.co.saiman.experiment.executor.Evaluation.PARALLEL;
import static uk.co.saiman.experiment.executor.Evaluation.SERIAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

import uk.co.saiman.experiment.conductor.IncomingDependencies.IncomingDependencyState;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

class OutgoingCondition<T> {
  private final OutgoingConditions conditions;
  private final Class<T> type;
  private final Evaluation evaluation;

  private final java.util.concurrent.locks.Condition lockCondition;

  private final HashMap<WorkspaceExperimentPath, IncomingCondition<T>> consumers = new LinkedHashMap<>();
  private final List<IncomingCondition<T>> acquiredConsumers = new ArrayList<>();
  private Supplier<? extends T> resource;

  public OutgoingCondition(OutgoingConditions conditions, Class<T> type, Evaluation evaluation) {
    this.conditions = conditions;
    this.type = type;
    this.evaluation = evaluation;

    this.lockCondition = conditions.lock().newCondition();
  }

  public boolean beginAcquire(IncomingCondition<T> conditionDependency) {
    if (resource == null) {
      return false;
    }
    // TODO switch statement in java 14
    boolean acquire;
    switch (evaluation) {
    case ORDERED:
      acquire = acquiredConsumers
          .stream()
          .allMatch(c -> c.getState() == IncomingDependencyState.DONE)
          && nextConsumer() == conditionDependency;
      break;
    case SERIAL:
      acquire = acquiredConsumers
          .stream()
          .allMatch(c -> c.getState() == IncomingDependencyState.DONE);
      break;
    default:
      acquire = true;
      break;
    }
    if (acquire) {
      this.acquiredConsumers.add(conditionDependency);
    }
    return acquire;
  }

  private IncomingCondition<T> nextConsumer() {
    return consumers
        .values()
        .stream()
        .filter(c -> !acquiredConsumers.contains(c))
        .findFirst()
        .orElse(null);
  }

  public void prepare(Supplier<? extends T> resource) {
    this.resource = Objects.requireNonNull(resource);

    try {
      while (consumers
          .values()
          .stream()
          .anyMatch(c -> c.getState() != IncomingDependencyState.DONE)) {
        lockCondition.signalAll();
        lockCondition.await();
      }
    } catch (InterruptedException e) {
      throw new ConductorException("Cancelled preparation", e);
    } finally {
      System.out.println("  ####### DONE PREPARE! " + consumers);
      resource = null;
    }
  }

  public IncomingCondition<T> addConsumer(WorkspaceExperimentPath path) {
    System.out.println("   adding consumer! " + path);
    return consumers.computeIfAbsent(path, p -> new IncomingCondition<>(this, lockCondition));
  }

  public void invalidate() {
    consumers.values().forEach(IncomingCondition::invalidatedOutgoing);
    consumers.clear();
    acquiredConsumers.clear();
  }

  public void invalidatedIncoming(IncomingCondition<T> incoming) {
    if (acquiredConsumers.contains(incoming) && (evaluation == ORDERED
        || evaluation == PARALLEL || evaluation == SERIAL)) {
      invalidate();
    }
  }

  public void terminate() {
    consumers.values().forEach(IncomingCondition::done);
  }

  Lock lock() {
    return conditions.lock();
  }

  WorkspaceExperimentPath path() {
    return conditions.path();
  }

  public Class<T> type() {
    return type;
  }

  T nextResource() {
    return resource.get();
  }
}