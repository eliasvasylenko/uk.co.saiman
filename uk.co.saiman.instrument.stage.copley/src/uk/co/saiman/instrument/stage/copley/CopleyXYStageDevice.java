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
 * This file is part of uk.co.saiman.instrument.stage.
 *
 * uk.co.saiman.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.copley;

import static uk.co.saiman.comms.Comms.CommsStatus.OPEN;
import static uk.co.saiman.instrument.HardwareConnection.CONNECTED;
import static uk.co.saiman.instrument.HardwareConnection.DISCONNECTED;

import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.instrument.HardwareConnection;
import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.measurement.Units;
import uk.co.strangeskies.observable.ObservableValue;
import uk.co.strangeskies.text.properties.PropertyLoader;

@Component
public class CopleyXYStageDevice implements XYStageDevice {
  @Reference
  private PropertyLoader loader;
  private CopleyStageProperties properties;

  @Reference
  private CopleyComms comms;
  private CopleyController controller;

  @Reference
  private Units units;

  @Activate
  private void activate() {
    properties = loader.getProperties(CopleyStageProperties.class);
  }

  @Override
  public String getName() {
    return properties.name().get();
  }

  public boolean isConnected() {
    return comms.status().isEqual(OPEN);
  }

  public void reset() {
    comms.reset();
    controller = comms.openController();
  }

  @Override
  public StageDimension<Length> getXAxis() {
    return new CopleyLinearDimension(units, controller.getAxis(0), controller);
  }

  @Override
  public StageDimension<Length> getYAxis() {
    return new CopleyLinearDimension(units, controller.getAxis(1), controller);
  }

  @Override
  public ObservableValue<HardwareConnection> connectionState() {
    return comms.status().map(s -> s == OPEN ? CONNECTED : DISCONNECTED).toValue();
  }
}
