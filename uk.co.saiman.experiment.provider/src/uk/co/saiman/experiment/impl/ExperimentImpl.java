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

import static uk.co.saiman.collection.StreamUtilities.reverse;

import java.io.IOException;
import java.util.stream.Stream;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

public class ExperimentImpl extends ExperimentNodeImpl<ExperimentConfiguration, Void>
    implements Experiment {
  private final WorkspaceImpl workspace;

  /**
   * Create a root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  protected ExperimentImpl(WorkspaceImpl workspace, String id) {
    super(workspace.getExperimentRootType(), id);
    this.workspace = workspace;
  }

  /**
   * Load a root experiment.
   * 
   * @param workspace
   * @param type
   * @param id
   */
  protected ExperimentImpl(WorkspaceImpl workspace, PersistedExperiment persistedExperiment) {
    super(workspace.getExperimentRootType(), persistedExperiment);
    this.workspace = workspace;

    loadChildNodes();
  }

  @Override
  public WorkspaceImpl getWorkspace() {
    return workspace;
  }

  @Override
  protected ExperimentProperties getText() {
    return workspace.getText();
  }

  @Override
  protected Log getLog() {
    return workspace.getLog();
  }

  @Override
  protected ExperimentLocationManager getLocationManager() {
    return workspace.getLocationManager();
  }

  @Override
  protected ExperimentPersistenceManager getPersistenceManager() {
    return workspace.getPersistenceManager();
  }

  @Override
  public void removeImpl() {
    if (!getWorkspace().removeExperiment(getExperiment())) {
      ExperimentException e = new ExperimentException(
          getText().exception().experimentDoesNotExist(getId()));
      getLog().log(Level.ERROR, e);
      throw e;
    }

    try {
      getLocationManager().removeLocation(this);
      getPersistenceManager().removeExperiment(getPersistedExperiment());
    } catch (IOException e) {
      ExperimentException ee = new ExperimentException(
          getText().exception().cannotRemoveExperiment(this),
          e);
      getLog().log(Level.ERROR, ee);
      throw ee;
    }
  }

  @Override
  public void execute() {
    boolean success = reverse(getAncestorsImpl())
        .filter(ExperimentNodeImpl::executeImpl)
        .count() > 0;

    if (success) {
      processChildren();
    }
  }

  @Override
  protected Stream<? extends ExperimentNode<?, ?>> getSiblings() {
    return getWorkspace().getExperiments();
  }
}
