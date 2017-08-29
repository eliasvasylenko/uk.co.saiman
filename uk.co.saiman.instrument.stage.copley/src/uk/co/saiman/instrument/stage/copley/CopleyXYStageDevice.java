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
package uk.co.saiman.instrument.stage.copley;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.stage.StageDimension;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.instrument.stage.copley.CopleyXYStageDevice.CopleyXYStageConfiguration;

@Designate(ocd = CopleyXYStageConfiguration.class, factory = true)
@Component(configurationPid = CopleyXYStageDevice.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class CopleyXYStageDevice extends CopleyStageDevice implements XYStageDevice {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Copley XY Stage Configuration",
      description = "An implementation of an XY stage device interface based on copley motor hardware")
  public @interface CopleyXYStageConfiguration {

  }

  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.copley.xy";

  private StageDimension<Length> xAxis;
  private StageDimension<Length> yAxis;

  @Activate
  void activate(CopleyXYStageConfiguration configuration) {
    activate();
    xAxis = new CopleyLinearDimension(getUnits(), 0, getController());
    yAxis = new CopleyLinearDimension(getUnits(), 1, getController());
  }

  @Override
  public String getName() {
    return getProperties().copleyXYStageName().get();
  }

  @Override
  public StageDimension<Length> getXAxis() {
    return xAxis;
  }

  @Override
  public StageDimension<Length> getYAxis() {
    return yAxis;
  }
}
