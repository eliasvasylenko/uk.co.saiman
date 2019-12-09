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
 * This file is part of uk.co.saiman.simulation.msapex.
 *
 * uk.co.saiman.simulation.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.msapex.device.DevicePresenter;
import uk.co.saiman.simulation.instrument.impl.SimulatedAcquisitionDevice;

@ServiceRanking(10)
@Component
public class SimulatedAcquisitionDevicePresenter implements DevicePresenter {
  public SimulatedAcquisitionDevicePresenter() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public String getLocalizedLabel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getIconURI() {
    return null;
  }

  @Override
  public boolean presentsDevice(Device<?> device) {
    return device instanceof SimulatedAcquisitionDevice;
  }
}
