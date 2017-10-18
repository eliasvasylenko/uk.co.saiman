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
import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * Add an experiment to the workspace.
 * 
 * @author Elias N Vasylenko
 */
public class RunExperimentHandler {
  /**
   * The ID of the command in the e4 model fragment.
   */
  public static final String COMMAND_ID = "uk.co.saiman.msapex.experiment.command.runexperiment";

  @Execute
  void execute(
      @Localize ExperimentProperties text,
      @AdaptNamed(ACTIVE_SELECTION) TreeEntry<?> entry) {
    TreeEntry<?> ancestor = entry;

    while (!ancestor.type().isAssignableTo(new TypeToken<ExperimentNode<?, ?>>() {})) {
      ancestor = ancestor.parent().orElseThrow(
          () -> new ExperimentException(
              text.exception().illegalCommandForSelection(COMMAND_ID, entry)));
    }

    ExperimentNode<?, ?> experimentNode = (ExperimentNode<?, ?>) ancestor.data();

    new Thread(experimentNode::process).start();
  }
}
