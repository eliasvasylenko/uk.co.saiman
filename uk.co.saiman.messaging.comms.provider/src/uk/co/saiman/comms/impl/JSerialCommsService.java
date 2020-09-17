/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.messaging.comms.provider.
 *
 * uk.co.saiman.messaging.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.messaging.comms.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static com.fazecast.jSerialComm.SerialPort.getCommPorts;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.tryOptional;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.log.Log;

@Component
public class JSerialCommsService {
  private List<ServiceRegistration<CommsPort>> ports;

  @Activate
  public JSerialCommsService(BundleContext context, @Reference Log log) {
    ports = stream(getCommPorts())
        .map(p -> tryOptional(() -> new JSerialPortService(p, log)))
        .flatMap(StreamUtilities::streamOptional)
        .map(port -> context.registerService(CommsPort.class, port, getProperties(port)))
        .collect(toList());
  }

  private Dictionary<String, String> getProperties(JSerialPortService port) {
    Dictionary<String, String> properties = new Hashtable<>();
    properties.put(JSerialPortService.NAME, port.getName());
    return properties;
  }

  @Deactivate
  void deactivate(BundleContext context) {
    for (ServiceRegistration<CommsPort> port : ports) {
      port.unregister();
    }
  }
}
