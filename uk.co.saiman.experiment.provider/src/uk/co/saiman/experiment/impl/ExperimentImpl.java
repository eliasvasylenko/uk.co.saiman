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

import java.io.IOException;
import java.util.stream.Stream;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.log.Log.Level;

public class ExperimentImpl extends ExperimentNodeImpl<ExperimentConfiguration, Void>
    implements Experiment {
  /**
   * Create a root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  protected ExperimentImpl(WorkspaceImpl workspace, String id) {
    super(workspace, workspace.getExperimentRootType(), id);
  }

  /**
   * Load a root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  protected ExperimentImpl(WorkspaceImpl workspace, PersistedExperiment persistedExperiment) {
    super(workspace, workspace.getExperimentRootType(), persistedExperiment);
  }

  @Override
  public void removeImpl() {
    if (!getWorkspace().removeExperiment(getExperiment())) {
      ExperimentException e = new ExperimentException(
          format("Experiment %s does not exist", getId()));
      getLog().log(Level.ERROR, e);
      throw e;
    }

    try {
      getLocationManager().removeLocation(this);
      getPersistenceManager().removeExperiment(getPersistedExperiment());
    } catch (IOException e) {
      ExperimentException ee = new ExperimentException(
          format("Cannot remove experiment %s", getId()),
          e);
      getLog().log(Level.ERROR, ee);
      throw ee;
    }
  }

  @Override
  protected Stream<? extends ExperimentNode<?, ?>> getSiblings() {
    return getWorkspace().getExperiments();
  }
}
