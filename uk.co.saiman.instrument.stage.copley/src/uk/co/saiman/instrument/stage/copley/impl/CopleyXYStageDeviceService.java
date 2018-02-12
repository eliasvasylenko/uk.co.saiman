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
 * This file is part of uk.co.saiman.instrument.stage.copley.
 *
 * uk.co.saiman.instrument.stage.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.copley.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.instrument.stage.copley.CopleyXYStageDevice;
import uk.co.saiman.instrument.stage.copley.CopleyXYStageDevice.CopleyXYStageConfiguration;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.text.properties.PropertyLoader;

@Designate(ocd = CopleyXYStageConfiguration.class, factory = true)
@Component(
    configurationPid = CopleyXYStageDeviceService.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyXYStageDeviceService extends CopleyXYStageDevice implements XYStageDevice {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.copley.xy";

  @Reference
  Instrument instrument;

  @Reference
  PropertyLoader loader;

  @Reference
  Units units;

  @Reference
  CopleyComms commsX;

  @Reference
  CopleyComms commsY;

  @Activate
  void activate(CopleyXYStageConfiguration configuration) {
    initialize(instrument, commsX, commsY, loader);
    configure(configuration, units);
  }

  @Modified
  void modified(CopleyXYStageConfiguration configuration) {
    configure(configuration, units);
  }

  @Override
  public String getName() {
    return getProperties().copleyXYStageName().get();
  }
}
