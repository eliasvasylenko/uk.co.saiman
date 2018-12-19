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

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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
class ExperimentLock {
  private final Set<ExperimentStep<?>> experimentNodes;
  private final Set<Experiment> experiments;

  public ExperimentLock(Collection<? extends ExperimentStep<?>> experimentNodes) {
    this.experimentNodes = new HashSet<>(experimentNodes);
    this.experiments = experimentNodes
        .stream()
        .map(ExperimentStep::getExperiment)
        .flatMap(Optional::stream)
        .collect(toSet());
  }

  public Stream<ExperimentStep<?>> getExperimentNodes() {
    return experimentNodes.stream();
  }

  public Stream<Experiment> getExperiments() {
    return experiments.stream();
  }
}