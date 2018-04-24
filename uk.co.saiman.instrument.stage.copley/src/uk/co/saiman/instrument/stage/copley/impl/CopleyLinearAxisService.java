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

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.composed.StageAxis;
import uk.co.saiman.instrument.stage.copley.CopleyLinearAxis;
import uk.co.saiman.observable.ObservableValue;

/**
 * TODO this class should not need to exist. Fold the annotations into
 * {@link CopleyLinearAxis} using constructor injection with R7.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = CopleyLinearAxisService.CopleyLinearAxisConfiguration.class, factory = true)
@Component(
    name = CopleyLinearAxisService.CONFIGURATION_PID,
    configurationPid = CopleyLinearAxisService.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyLinearAxisService implements StageAxis<Length> {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.copley.linear";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "Copley Stage Linear Axis Configuration",
      description = "The configuration for a linear Copley motor axis for a modular stage")
  public @interface CopleyLinearAxisConfiguration {
    @AttributeDefinition(
        name = "Controller comms target",
        description = "The copley controller instance owning the axis")
    String comms_target();

    int axis() default 0;

    int node() default 0;
  }

  @Reference
  private CopleyController comms;

  private CopleyLinearAxis axis;

  @Activate
  void activate(CopleyLinearAxisConfiguration configuration) {
    this.axis = new CopleyLinearAxis(comms, configuration.node(), configuration.axis());
  }

  @Override
  public ObservableValue<Quantity<Length>> actualLocation() {
    return axis.actualLocation();
  }

  @Override
  public ObservableValue<ConnectionState> connectionState() {
    return axis.connectionState();
  }

  @Override
  public ObservableValue<SampleState> sampleState() {
    return axis.sampleState();
  }

  @Override
  public void abortRequest() {
    axis.abortRequest();
  }

  @Override
  public void requestLocation(Quantity<Length> location) {
    axis.requestLocation(location);
  }
}
