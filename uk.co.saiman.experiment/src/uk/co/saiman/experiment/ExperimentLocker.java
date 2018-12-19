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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility to lock on all event sources participating in an event dispatch.
 * Avoids deadlock by selecting an arbitrary winner by locking on the class.
 * <p>
 * When we send an event it must be dispatched from the node in question as well
 * as to all ancestor nodes, so we need to lock on them all first to make sure
 * they don't shift underneath us and we dispatch events from the wrong nodes.
 * <p>
 * This almost is a neat little system, as we avoid deadlock by virtue of having
 * a natural ordering defined by the parent-child relationship. Unfortunately we
 * sometimes need to dispatch two events atomically from different stacks, in
 * particular for
 * 
 * @author Elias N Vasylenko
 */
class ExperimentLocker {
  private final List<ExperimentStep<?>> experimentNodes;

  public ExperimentLocker(Collection<? extends ExperimentStep<?>> experimentNodes) {
    this.experimentNodes = new ArrayList<>(experimentNodes);
  }

  /*
   * 
   * 
   * 
   * 
   * 
   * TODO lock on each chain of event sources in order.
   * 
   * If we get blocked by something which is already owned by another
   * WorkspaceEventLock AND we have more than one event source AND the other has
   * more than one event source then lock on this class (not instance) and
   * relinquish all locks until the other is finished.
   * 
   * 
   * 
   * 
   * 
   */

  public void update(Consumer<ExperimentLock> action) {
    get(lock -> {
      action.accept(lock);
      return null;
    });
    experimentNodes
        .stream()
        .map(ExperimentStep::getExperiment)
        .flatMap(Optional::stream)
        .distinct()
        .forEach(Experiment::fireEvents);
  }

  public <T> T get(Function<ExperimentLock, T> action) {
    // TODO safely lock on all experiment nodes and their parents

    synchronized (ExperimentStep.class) {
      var lock = new ExperimentLock(experimentNodes);
      return action.apply(lock);
    }
  }
}