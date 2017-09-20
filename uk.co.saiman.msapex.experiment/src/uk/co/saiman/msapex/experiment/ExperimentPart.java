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

import uk.co.saiman.eclipse.treeview.EclipseModularTreeController;
import uk.co.saiman.experiment.Workspace;

/**
 * Experiment management view part. Manage experiments and their results in the
 * experiment tree.
 * 
 * @author Elias N Vasylenko
 */
/*
 * Specify a service capability requirement on the ExperimentWorkspaceFactory
 * injection via the bundle manifest.
 */
public interface ExperimentPart {
  /**
   * The ID of the command in the e4 model fragment.
   */
  public static final String ADD_EXPERIMENT_COMMAND = "uk.co.saiman.msapex.experiment.command.addexperiment";

  /**
   * The ID of the command in the e4 model fragment.
   */
  public static final String OPEN_EXPERIMENT_COMMAND = "uk.co.saiman.msapex.command.open";

  /**
   * @return the current experiment workspace
   */
  Workspace getExperimentWorkspace();

  /**
   * @return the controller for the experiment tree UI item
   */
  EclipseModularTreeController getExperimentTreeController();
}
