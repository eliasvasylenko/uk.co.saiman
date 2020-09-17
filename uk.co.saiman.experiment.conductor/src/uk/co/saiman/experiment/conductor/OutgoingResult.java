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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

import uk.co.saiman.experiment.conductor.IncomingDependencies.IncomingDependencyState;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

class OutgoingResult<T> {
  private final OutgoingResults results;
  private final Class<T> type;

  private final java.util.concurrent.locks.Condition lockCondition;

  private final HashMap<WorkspaceExperimentPath, IncomingResult<T>> consumers = new LinkedHashMap<>();
  private final List<IncomingResult<T>> acquiredResults = new ArrayList<>();
  private T resource;

  public OutgoingResult(OutgoingResults results, Class<T> type) {
    this.results = results;
    this.type = type;

    this.lockCondition = results.lock().newCondition();
  }

  public boolean beginAcquire(IncomingResult<T> resultDependency) {
    if (resource == null) {
      return false;
    }
    this.acquiredResults.add(resultDependency);
    return true;
  }

  public void prepare(T resource) {
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

  public IncomingResult<T> addConsumer(WorkspaceExperimentPath path) {
    System.out.println("   adding consumer! " + path);
    return consumers.computeIfAbsent(path, p -> new IncomingResult<>(this, lockCondition));
  }

  public void invalidate() {
    consumers.values().forEach(IncomingResult::invalidatedOutgoing);
    consumers.clear();
    acquiredResults.clear();
  }

  public void terminate() {
    consumers.values().forEach(IncomingResult::done);
  }

  Lock lock() {
    return results.lock();
  }

  WorkspaceExperimentPath path() {
    return results.path();
  }

  public Class<T> type() {
    return type;
  }

  T resource() {
    return resource;
  }
}