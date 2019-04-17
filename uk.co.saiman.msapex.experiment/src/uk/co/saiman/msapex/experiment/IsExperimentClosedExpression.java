/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Optional;

import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment;
import uk.co.saiman.msapex.experiment.workspace.WorkspaceExperiment.Status;
import uk.co.saiman.msapex.experiment.workspace.event.CloseExperimentEvent;
import uk.co.saiman.msapex.experiment.workspace.event.OpenExperimentEvent;

public class IsExperimentClosedExpression {
  @Evaluate
  public boolean evaluate(
      @Optional WorkspaceExperiment experiment,
      @Optional OpenExperimentEvent openEvent,
      @Optional CloseExperimentEvent closeEvent) {
    return experiment != null && experiment.status() == Status.CLOSED;
  }
}
