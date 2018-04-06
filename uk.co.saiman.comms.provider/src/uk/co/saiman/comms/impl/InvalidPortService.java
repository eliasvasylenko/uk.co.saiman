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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.comms.InvalidCommsPort;
import uk.co.saiman.comms.impl.InvalidPortService.InvalidCommsPortConfiguration;

@Designate(ocd = InvalidCommsPortConfiguration.class, factory = true)
@Component(configurationPid = InvalidPortService.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class InvalidPortService implements CommsPort {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Invalid Port Configuration",
      description = "A simple simulation of an invalid serial port which does not accept connections")
  public @interface InvalidCommsPortConfiguration {
    @AttributeDefinition(name = "Port Name", description = "The name of the port to provide")
    String name();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.comms.simulation.invalid";

  private InvalidCommsPort component;

  @Activate
  void activate(InvalidCommsPortConfiguration configuration) {
    component = new InvalidCommsPort(configuration.name());
  }

  @Override
  public String getName() {
    return component.getName();
  }

  @Override
  public boolean isOpen() {
    return component.isOpen();
  }

  @Override
  public void kill() {
    component.kill();
  }

  @Override
  public CommsChannel openChannel() {
    return component.openChannel();
  }

  @Override
  public CommsStream openStream(int packetSize) {
    return component.openStream(packetSize);
  }
}
