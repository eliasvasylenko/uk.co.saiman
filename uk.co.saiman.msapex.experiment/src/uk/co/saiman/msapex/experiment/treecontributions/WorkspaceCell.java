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
package uk.co.saiman.msapex.experiment.treecontributions;

import static java.util.stream.Collectors.toList;
import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;

import javax.inject.Named;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.eclipse.ui.ListItems;
import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.experiment.Workspace;

@Component(name = WorkspaceCell.ID, service = MCell.class)
public class WorkspaceCell extends MCellImpl {
  public static final String ID = "uk.co.saiman.experiment.cell.workspace";
  public static final String EXPERIMENTS_ID = ID + ".experiments";

  public WorkspaceCell() {
    super(ID, Contribution.class);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + ExperimentNodeCell.ID + ")")
  public void setChild(MCell nodes) {
    MCellImpl child = new MCellImpl(EXPERIMENTS_ID, null);
    child.setSpecialized(nodes);
    child.setParent(this);
  }

  public static class Contribution {
    @AboutToShow
    public void prepare(ListItems children, @Named(ENTRY_DATA) Workspace workspace) {
      children
          .getConfiguration(EXPERIMENTS_ID)
          .setObjects(workspace.getExperiments().collect(toList()));
    }
  }
}
