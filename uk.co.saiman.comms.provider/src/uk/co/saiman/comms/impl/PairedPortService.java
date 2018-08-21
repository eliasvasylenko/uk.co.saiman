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
 * This file is part of uk.co.saiman.comms.provider.
 *
 * uk.co.saiman.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.impl.PairedPortService.PairedCommsPortConfiguration;
import uk.co.saiman.log.Log;

@Designate(ocd = PairedCommsPortConfiguration.class, factory = true)
@Component(configurationPid = PairedPortService.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class PairedPortService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Paired Port Configuration",
      description = "A simple simulation of a pair of serial ports connected in a two-way pipe")
  public @interface PairedCommsPortConfiguration {
    @AttributeDefinition(name = "Port Name", description = "The name of the port to provide")
    String name();

    @AttributeDefinition(
        name = "Partner Port Name",
        description = "The name of the partner port to provide")
    String partnerName();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.comms.simulation.paired";
  static final String NAME = "name";

  @Reference
  private Log log;

  private ServiceRegistration<CommsPort> portService;
  private ServiceRegistration<CommsPort> pairedPortService;

  @Activate
  void activate(BundleContext context, PairedCommsPortConfiguration configuration) {
    try {
      PairedCommsPort port = new PairedCommsPort(configuration.name(), configuration.partnerName());

      portService = registerService(context, port);
      pairedPortService = registerService(context, port.getPartner());
    } catch (Exception e) {
      Log log = this.log;
      if (log != null)
        log.log(ERROR, e);
      e.printStackTrace();
    }
  }

  private ServiceRegistration<CommsPort> registerService(
      BundleContext context,
      PairedCommsPort port) {
    Dictionary<String, String> properties = new Hashtable<>();
    properties.put(NAME, port.getName());
    return context.registerService(CommsPort.class, port, properties);
  }

  @Deactivate
  void deactivate() {
    if (portService != null)
      portService.unregister();
    if (pairedPortService != null)
      pairedPortService.unregister();
  }
}
