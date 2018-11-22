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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import static java.util.stream.Collectors.toList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.eclipse.ui.ChildrenService;
import uk.co.saiman.msapex.experiment.workspace.Workspace;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;
import uk.co.saiman.msapex.experiment.workspace.event.AddExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.RemoveExperimentEvent;

public class ExperimentTree {
  public static final String ID = "uk.co.saiman.msapex.experiment.tree";

  @Inject
  private Workspace workspace;

  @Inject
  private ChildrenService children;

  @PostConstruct
  void initialize() {
    updateChildren();
  }

  @Inject
  @Optional
  public void update(AddExperimentEvent event) {
    updateChildren();
  }

  @Inject
  @Optional
  public void update(RemoveExperimentEvent event) {
    updateChildren();
  }

  private void updateChildren() {
    children
        .setItems(
            ExperimentCell.ID,
            WorkspaceExperiment.class,
            workspace.getWorkspaceExperiments().collect(toList()));
  }
}
