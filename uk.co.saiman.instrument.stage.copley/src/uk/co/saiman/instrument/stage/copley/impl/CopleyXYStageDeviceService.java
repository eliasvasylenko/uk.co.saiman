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
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.instrument.stage.copley.CopleyXYStageDevice;
import uk.co.saiman.instrument.stage.copley.impl.CopleyXYStageDeviceService.CopleyXYStageConfiguration;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.text.properties.PropertyLoader;

@Designate(ocd = CopleyXYStageConfiguration.class, factory = true)
@Component(
    configurationPid = CopleyXYStageDeviceService.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyXYStageDeviceService extends CopleyXYStageDevice implements XYStageDevice {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.copley.xy";

  @ObjectClassDefinition(
      name = "Copley XY Stage Configuration",
      description = "An implementation of an XY stage device interface based on copley motor hardware")
  public @interface CopleyXYStageConfiguration {
    @AttributeDefinition(
        name = "Copley Comms",
        description = "The OSGi reference filter for the comms interface")
    String lowerBoundX();

    String lowerBoundY();

    String upperBoundX();

    String upperBoundY();

    String homePositionX();

    String homePositionY();

    String exchangePositionX();

    String exchangePositionY();
  }

  @Reference
  Instrument instrument;

  @Reference
  PropertyLoader loader;

  @Reference
  Units units;

  @Reference(cardinality = OPTIONAL)
  CopleyAxis xAxis;
  @Reference(cardinality = OPTIONAL)
  CopleyAxis yAxis;

  @Activate
  void activate(CopleyXYStageConfiguration configuration) {
    initialize(instrument, xAxis, yAxis, loader);
    modified(configuration);
  }

  /*
   * TODO restructure this whole mess into using constructor injection, then at
   * that time, restructure into having a general XYStageDeviceService which
   * has @Requirements on StageDimension services, and have a DS service factory
   * CopleyLinearDimension.
   */

  @Modified
  void modified(CopleyXYStageConfiguration configuration) {
    configure(
        parseLength(configuration.lowerBoundX()),
        parseLength(configuration.lowerBoundY()),
        parseLength(configuration.upperBoundX()),
        parseLength(configuration.upperBoundY()),
        parseLength(configuration.homePositionX()),
        parseLength(configuration.homePositionY()),
        parseLength(configuration.exchangePositionX()),
        parseLength(configuration.exchangePositionY()),
        units);
  }

  Quantity<Length> parseLength(String string) {
    return units.parseQuantity(string).asType(Length.class);
  }

  @Override
  public String getName() {
    return getProperties().copleyXYStageName().get();
  }
}
