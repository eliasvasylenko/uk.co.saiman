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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static java.lang.String.format;
import static uk.co.saiman.experiment.ExperimentLifecycleState.CONFIGURATION;

import java.util.stream.Stream;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ResultStore;
import uk.co.saiman.experiment.impl.WorkspaceEventImpl.RemoveExperimentEventImpl;
import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.log.Log.Level;

public class ExperimentImpl extends ExperimentNodeImpl<ExperimentConfiguration, Void>
    implements Experiment {
  private final ResultStore locationManager;

  /**
   * Load a root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  protected ExperimentImpl(
      ResultStore locationManager,
      String id,
      StateMap persistedState,
      WorkspaceImpl workspace) {
    super(id, workspace.getExperimentRootType(), persistedState, workspace, CONFIGURATION);
    this.locationManager = locationManager;
  }

  public ResultStore getLocationManager() {
    return locationManager;
  }

  @Override
  public void remove() {
    getWorkspace().fireEvents(() -> {
      setDisposed();
      removeImpl();
      return null;
    }, new RemoveExperimentEventImpl(this, null));
  }

  @Override
  protected void removeImpl() {
    if (!getWorkspace().removeExperiment(getExperiment())) {
      ExperimentException e = new ExperimentException(
          format("Experiment %s does not exist", getId()));
      getLog().log(Level.ERROR, e);
      throw e;
    }

    clearResult();
  }

  @Override
  protected Stream<? extends ExperimentNode<?, ?>> getSiblings() {
    return getWorkspace().getExperiments();
  }
}
