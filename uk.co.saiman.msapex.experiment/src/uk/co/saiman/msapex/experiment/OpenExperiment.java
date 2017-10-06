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

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.eclipse.AdaptNamed;
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;

/**
 * Add an experiment to the workspace.
 * 
 * @author Elias N Vasylenko
 */
public class OpenExperiment {
  @CanExecute
  boolean canExecute(
      @Optional @AdaptNamed(ACTIVE_SELECTION) ExperimentNode<?, ?> selectedNode,
      @Localize ExperimentProperties text) {
    System.out.println(
        "has results? " + selectedNode != null
            && selectedNode.getType().getResultTypes().findAny().isPresent());

    return selectedNode != null && selectedNode.getType().getResultTypes().findAny().isPresent();
  }

  @Execute
  void execute(
      @AdaptNamed(ACTIVE_SELECTION) ExperimentNode<?, ?> selectedNode,
      ResultEditorManager editorManager) {
    editorManager.openEditor(selectedNode.getResults().findFirst().get());
  }
}
