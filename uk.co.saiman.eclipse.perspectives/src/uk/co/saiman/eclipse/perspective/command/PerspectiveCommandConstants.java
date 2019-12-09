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

public final class PerspectiveCommandConstants {
  private PerspectiveCommandConstants() {}

  public static final String RESET_COMMAND_ID = "uk.co.saiman.command.resetperspective";
  public static final String SHOW_COMMAND_ID = "uk.co.saiman.command.showperspective";

  public static final String PERSPECTIVE_ID_PARAMETER = "uk.co.saiman.commandparameter.perspectiveid";
  public static final String PERSPECTIVE_STACK_ID_PARAMETER = "uk.co.saiman.commandparameter.perspectivestackid";

  public static final String DEFAULT_PART_PLACEHOLDER_CONTAINER = "DefaultPartPlaceholderContainer";
}
