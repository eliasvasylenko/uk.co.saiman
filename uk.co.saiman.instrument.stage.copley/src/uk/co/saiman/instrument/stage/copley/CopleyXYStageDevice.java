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

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.instrument.HardwareConnection;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.strangeskies.observable.ObservableValue;
import uk.co.strangeskies.text.properties.PropertyLoader;

@Component
public class CopleyXYStageDevice implements XYStageDevice {
  @Reference
  private PropertyLoader loader;
  private CopleyStageProperties properties;

  @Reference
  private CopleyComms comms;

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
  }

  @Override
  public Quantity<Length> getStageWidth() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Quantity<Length> getStageHeight() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void requestStageOffset(Quantity<Length> x, Quantity<Length> y) {
    // TODO Auto-generated method stub

  }

  @Override
  public Quantity<Length> getRequestedStageX() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Quantity<Length> getRequestedStageY() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Quantity<Length> getActualStageX() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Quantity<Length> getActualStageY() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ObservableValue<HardwareConnection> connectionState() {
    // TODO Auto-generated method stub
    return null;
  }
}
