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
package uk.co.saiman.eclipse.perspective;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

public interface EPerspectiveService {
  public static final String PERSPECTIVE_SOURCE_SNIPPET = "PerspectiveSourceSnippet";
  public static final String PERSPECTIVE_TARGET_STACK = "PerspectiveTargetStack";

  /*
   * TODO when we drag a part out of a perspective into a new window with the dnd
   * addon, we need to remember which perspective the new window is associated
   * with and hide it when the perspective is changed. Ideally we want to find a
   * way to do this without any coupling between this and the dnd service...
   */

  IPerspectiveContainer getActiveContainer();

  IPerspectiveContainer findContainer(String perspectiveStackId);

  MPerspective findPerspective(String perspectiveId);

  void resetPerspective(MPerspective perspective);

  void activatePerspective(MPerspective perspective);
}
