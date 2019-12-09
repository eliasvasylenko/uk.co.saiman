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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.Service;

import uk.co.saiman.camera.CameraDevice;

/**
 * Select the next camera device in the eclipse context, if another is
 * available.
 * <p>
 * If the previous device was connected, disconnect it and connect the new one,
 * setting the new connection in the eclipse context.
 *
 * @author Elias N Vasylenko
 */
public class SelectNextCameraHandler {
  @Inject
  @Service
  List<CameraDevice> availableDevices;

  @Execute
  void execute(IEclipseContext context, @Optional CameraDevice selectedDevice) {
    List<CameraDevice> availableDevices = new ArrayList<>(this.availableDevices);
    int index = availableDevices.indexOf(selectedDevice);
    if (++index == availableDevices.size())
      index = 0;

    CameraSelectionHelper.selectCamera(context, availableDevices.get(index));
  }
}
