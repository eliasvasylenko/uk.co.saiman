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

import static com.fazecast.jSerialComm.SerialPort.getCommPorts;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.fazecast.jSerialComm.SerialPort;

import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.impl.JSerialCommsImpl.JSerialPortConfiguration;
import uk.co.saiman.log.Log;

@Designate(ocd = JSerialPortConfiguration.class)
@Component(
    immediate = true,
    configurationPid = JSerialCommsImpl.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL)
public class JSerialCommsImpl {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "JSerialPort Comms Configuration",
      description = "The JSerialPort component provides native serial port interfaces")
  public @interface JSerialPortConfiguration {
    @AttributeDefinition(
        name = "Named Ports",
        description = "A list of extra port names to provide alongside any automatically detected ports")
    String[] namedPorts() default {};
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.comms.jserialport";
  public static final String NAME = "name";

  @Reference
  private Log log;

  private List<ServiceRegistration<CommsPort>> ports;

  @Activate
  void activate(BundleContext context, JSerialPortConfiguration configuration) {
    try {
      String[] namedPorts = configuration.namedPorts();
      namedPorts = namedPorts == null ? new String[] {} : namedPorts;

      ports = Stream
          .concat(stream(getCommPorts()), stream(namedPorts).map(SerialPort::getCommPort))
          .map(JSerialCommsPort::new)
          .map(port -> context.registerService(CommsPort.class, port, getProperties(port)))
          .collect(toList());
    } catch (Exception e) {
      Log log = this.log;
      if (log != null)
        log.log(ERROR, e);
      e.printStackTrace();
    }
  }

  private Dictionary<String, String> getProperties(JSerialCommsPort port) {
    Dictionary<String, String> properties = new Hashtable<>();
    properties.put(NAME, port.getName());
    return properties;
  }

  @Deactivate
  void deactivate(BundleContext context) {
    for (ServiceRegistration<CommsPort> port : ports) {
      port.unregister();
    }
  }
}
