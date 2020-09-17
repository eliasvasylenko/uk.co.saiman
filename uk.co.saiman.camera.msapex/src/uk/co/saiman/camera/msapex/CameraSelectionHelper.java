/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.camera.msapex.
 *
 * uk.co.saiman.camera.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.camera.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.camera.msapex;

import org.eclipse.e4.core.contexts.IEclipseContext;

import uk.co.saiman.camera.CameraConnection;
import uk.co.saiman.camera.CameraDevice;

public class CameraSelectionHelper {
  private CameraSelectionHelper() {}

  public static void selectCamera(IEclipseContext context, CameraDevice selection) {
    CameraDevice currentDevice = context.get(CameraDevice.class);
    CameraConnection currentConnection = context.get(CameraConnection.class);

    if (currentDevice == selection)
      return;

    context.modify(CameraConnection.class, null);
    context.modify(CameraDevice.class, selection);

    if (currentConnection != null && !currentConnection.isDisposed()) {
      currentConnection.dispose();
      context.modify(CameraConnection.class, selection.openConnection());
    }
  }
}
