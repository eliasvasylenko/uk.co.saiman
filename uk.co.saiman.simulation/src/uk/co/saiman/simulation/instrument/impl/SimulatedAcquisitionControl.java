/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.instrument.impl;

import java.util.concurrent.TimeUnit;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import uk.co.saiman.acquisition.AcquisitionControl;
import uk.co.saiman.instrument.DeviceControlImpl;

public class SimulatedAcquisitionControl extends DeviceControlImpl<SimulatedAcquisitionDevice>
    implements AcquisitionControl {
  public SimulatedAcquisitionControl(
      SimulatedAcquisitionDevice device,
      long timeout,
      TimeUnit unit) {
    super(device, timeout, unit);
  }

  @Override
  public void startAcquisition() {
    getDevice().startAcquisition();
  }

  @Override
  public void setAcquisitionCount(int count) {
    getDevice().setAcquisitionCount(count);
  }

  @Override
  public void setAcquisitionTime(Quantity<Time> time) {
    getDevice().setAcquisitionTime(time);
  }

  @Override
  public void setSampleDepth(int depth) {
    getDevice().setSampleDepth(depth);
  }
}