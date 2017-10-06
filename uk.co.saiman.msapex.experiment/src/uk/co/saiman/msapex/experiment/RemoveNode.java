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

import static org.eclipse.e4.ui.services.IServiceConstants.ACTIVE_SELECTION;

import org.eclipse.e4.core.di.annotations.Execute;

import uk.co.saiman.eclipse.AdaptNamed;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.fx.TreeItemImpl;

/**
 * Remove a node from an experiment in the workspace
 * 
 * @author Elias N Vasylenko
 */
public class RemoveNode {
  @Execute
  void execute(
      @AdaptNamed(ACTIVE_SELECTION) ExperimentNode<?, ?> selectedNode,
      @AdaptNamed(ACTIVE_SELECTION) TreeItemImpl<?> item) {
    /*
     * TODO an "are you sure?" dialog. This is permanent!
     */
    
    selectedNode.remove();
    item.getData().parent().get().refresh(true);
  }
}
