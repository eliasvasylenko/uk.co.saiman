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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.eclipse.ui.Children;
import uk.co.saiman.eclipse.utilities.ContextBuffer;
import uk.co.saiman.experiment.msapex.workspace.Workspace;
import uk.co.saiman.experiment.msapex.workspace.WorkspaceExperiment;
import uk.co.saiman.experiment.msapex.workspace.event.AddExperimentEvent;
import uk.co.saiman.experiment.msapex.workspace.event.RemoveExperimentEvent;

public class ExperimentTree {
  public static final String ID = "uk.co.saiman.experiment.tree";

  @Inject
  private Workspace workspace;

  @Children(snippetId = ExperimentCell.ID)
  private Stream<ContextBuffer> updateChildren(
      @Optional AddExperimentEvent addition,
      @Optional RemoveExperimentEvent removal) {
    return workspace
        .getWorkspaceExperiments()
        .map(experiment -> ContextBuffer.empty().set(WorkspaceExperiment.class, experiment));
  }
}
