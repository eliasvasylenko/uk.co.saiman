/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
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
  /*
   * Root experiment types
   */

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
   * @param name
   *          the name of the new experiment
   * @return a new root experiment part of the root type
   */
  Experiment addExperiment(String name);

  /*
   * Child experiment types
   */

  /**
   * Register an available experiment type
   * 
   * @param experimentType
   *          a possible experiment type
   * @return true if the type was added successfully, false otherwise
   */
  boolean registerExperimentType(ExperimentType<?, ?> experimentType);

  /**
   * Unregister an available experiment type
   * 
   * @param experimentType
   *          a possible experiment type
   * @return true if the type was removed successfully, false otherwise
   */
  boolean unregisterExperimentType(ExperimentType<?, ?> experimentType);

  /**
   * @return the set of all experiment types registered to this workspace
   */
  Stream<ExperimentType<?, ?>> getRegisteredExperimentTypes();
}
