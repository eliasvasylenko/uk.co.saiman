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

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.observable.Observable;

/**
 * The concept of an experiment in a {@link Workspace workspace} is represented
 * by a hierarchy of nodes. The workspace provides an interface for managing
 * those experiments.
 * <p>
 * A workspace contains a register of {@link ExperimentType experiment types}.
 * Experiment nodes can be created according to these types.
 * 
 * @author Elias N Vasylenko
 */
public interface Workspace {
  /**
   * @return the root experiment type
   */
  ExperimentRoot getExperimentRootType();

  /**
   * Get all experiments of the {@link #getExperiments() root experiment type}.
   * 
   * @return all registered root experiment parts
   */
  Stream<Experiment> getExperiments();

  Optional<Experiment> getExperiment(String id);

  /**
   * Add a root experiment node to management.
   * 
   * @param id
   *          the name of the new experiment
   * @param locationManager
   *          the strategy for locating, reading, and writing experiment result
   *          data
   * @return a new root experiment part of the root type
   */
  Experiment addExperiment(String id, ResultStore locationManager);

  /**
   * @return an observable over workspace events in the given state
   */
  Observable<WorkspaceEvent> events(WorkspaceEventState state);

  /**
   * @return an observable over workspace events in the
   *         {@link WorkspaceEventState#COMPLETED completed} state
   */
  default Observable<WorkspaceEvent> events() {
    return events(WorkspaceEventState.COMPLETED);
  }
}
