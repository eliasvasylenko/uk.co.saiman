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

import static uk.co.saiman.experiment.ExperimentLifecycleState.PREPARATION;

import uk.co.saiman.experiment.state.StateMap;

public class Experiment extends ExperimentNode<ExperimentConfiguration> {
  private final StorageConfiguration<?> storageConfiguration;

  public Experiment(String id, StorageConfiguration<?> store) {
    this(id, StateMap.empty(), store);
  }

  public Experiment(String id, StateMap stateMap, StorageConfiguration<?> store) {
    this(ExperimentProcedure.instance(), id, stateMap, store);
  }

  protected Experiment(
      ExperimentProcedure procedure,
      String id,
      StateMap stateMap,
      StorageConfiguration<?> store) {
    super(procedure, id, stateMap, PREPARATION);
    this.storageConfiguration = store;
  }

  public StorageConfiguration<?> getStorageConfiguration() {
    return storageConfiguration;
  }

  @Override
  public int getIndex() {
    return -1;
  }

  public void dispose() {
    setDetached();
  }
}
