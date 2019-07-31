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
 * This file is part of uk.co.saiman.instrument.msapex.
 *
 * uk.co.saiman.instrument.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.msapex;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Service;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.msapex.device.DevicePresentation;
import uk.co.saiman.instrument.msapex.device.DevicePresentationService;

/**
 * Instrument management view part. For the management of hardware devices.
 * 
 * @author Elias N Vasylenko
 */
public class InstrumentAddon {
  @Service
  @Inject
  List<DevicePresentation> presentations;

  @PostConstruct
  void initialize(IEclipseContext context) {
    context.set(DevicePresentationService.class, new DevicePresentationService() {
      @Override
      public DevicePresentation present(Device<?> device) {
        return presentations
            .stream()
            .filter(p -> p.presentsDevice(device))
            .findFirst()
            .orElseGet(() -> new DefaultDevicePresentation(device));
      }
    });
  }
}
