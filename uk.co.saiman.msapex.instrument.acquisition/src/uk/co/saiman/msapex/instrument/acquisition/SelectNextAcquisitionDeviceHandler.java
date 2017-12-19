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
 * This file is part of uk.co.saiman.msapex.instrument.acquisition.
 *
 * uk.co.saiman.msapex.instrument.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.acquisition;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.Service;

import uk.co.saiman.acquisition.AcquisitionDevice;

/**
 * Track acquisition devices available through OSGi services and select which
 * device to display in the acquisition part.
 * 
 * @author Elias N Vasylenko
 */
public class SelectNextAcquisitionDeviceHandler {
  @Inject
  @Service
  List<AcquisitionDevice> availableDevices;

  @Execute
  void execute(IEclipseContext context, @Optional AcquisitionDeviceSelection selectedDevices) {
    List<AcquisitionDevice> availableDevices = new ArrayList<>(this.availableDevices);
    List<AcquisitionDevice> selection = selectedDevices != null
        ? selectedDevices.getSelectedDevices().collect(toList())
        : emptyList();

    int index = selection.size() != 1 ? -1 : availableDevices.indexOf(selection.get(0));
    if (++index == availableDevices.size())
      index = 0;

    context
        .modify(
            AcquisitionDeviceSelection.class,
            new AcquisitionDeviceSelection(singleton(availableDevices.get(index))));
  }
}
