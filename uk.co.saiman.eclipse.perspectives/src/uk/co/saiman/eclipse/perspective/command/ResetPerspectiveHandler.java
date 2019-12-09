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
 * This file is part of uk.co.saiman.eclipse.perspectives.
 *
 * uk.co.saiman.eclipse.perspectives is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.perspectives is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.perspective.command;

import static uk.co.saiman.eclipse.perspective.command.PerspectiveCommandConstants.PERSPECTIVE_ID_PARAMETER;
import static uk.co.saiman.eclipse.perspective.command.PerspectiveCommandConstants.PERSPECTIVE_STACK_ID_PARAMETER;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

import uk.co.saiman.eclipse.perspective.EPerspectiveService;
import uk.co.saiman.eclipse.perspective.IPerspectiveContainer;

public class ResetPerspectiveHandler {
  @Inject
  EPerspectiveService perspectiveService;

  @Execute
  public void execute(
      @Optional @Named(PERSPECTIVE_ID_PARAMETER) String perspectiveId,
      @Optional @Named(PERSPECTIVE_STACK_ID_PARAMETER) String perspectiveStackId) {
    IPerspectiveContainer perspectiveContainer;
    if (perspectiveStackId == null || perspectiveStackId.isBlank()) {
      perspectiveContainer = perspectiveService.getActiveContainer();
    } else {
      perspectiveContainer = perspectiveService.findContainer(perspectiveStackId);
    }

    if (perspectiveContainer == null) {
      return;
    }

    MPerspective perspective;
    if (perspectiveId == null || perspectiveId.isBlank()) {
      perspective = perspectiveContainer.getActivePerspective();
    } else {
      perspective = perspectiveContainer.findPerspective(perspectiveId);
    }

    if (perspective == null) {
      return;
    }

    perspectiveService.resetPerspective(perspective);
  }
}
