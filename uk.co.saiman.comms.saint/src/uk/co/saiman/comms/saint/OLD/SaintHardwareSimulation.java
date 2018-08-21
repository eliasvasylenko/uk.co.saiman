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
 * This file is part of uk.co.saiman.comms.saint.
 *
 * uk.co.saiman.comms.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.saint.OLD;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_RB_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_RB_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.LED_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.LED_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.MOTOR_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.MOTOR_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.SPARE_IO_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.SPARE_IO_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VACUUM_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VACUUM_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VACUUM_RB_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VACUUM_RB_PORT;
import static uk.co.saiman.comms.saint.SaintCommandType.fromByte;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.comms.saint.SaintCommandAddress;
import uk.co.saiman.comms.saint.SaintCommandType;
import uk.co.saiman.comms.saint.SaintController;
import uk.co.saiman.comms.saint.OLD.SaintHardwareSimulation.SaintHardwareSimulationConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

@Designate(ocd = SaintHardwareSimulationConfiguration.class, factory = true)
@Component(
    name = SaintHardwareSimulation.CONFIGURATION_PID,
    configurationPid = SaintHardwareSimulation.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class SaintHardwareSimulation {
  static final String CONFIGURATION_PID = "uk.co.saiman.comms.saint.simulation";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "SAINT Comms Hardware Simulation Configuration",
      description = "A configuration for a simulation of the SAINT instrument comms board")
  public @interface SaintHardwareSimulationConfiguration {
    @AttributeDefinition(
        name = "Serial Port",
        description = "The serial port for the hardware simulation")
    String port_target();
  }

  @Reference
  private Log log;

  @Reference(policyOption = GREEDY)
  private CommsPort port;
  private CommsStream stream;

  private List<Byte> memory = new ArrayList<>();

  private Map<Integer, Integer> actualToRequest = new HashMap<>();

  public SaintHardwareSimulation() {
    mapActualToRequest(LED_LAT, LED_PORT);
    mapActualToRequest(VACUUM_LAT, VACUUM_PORT);
    mapActualToRequest(HV_LAT, HV_PORT);
    mapActualToRequest(MOTOR_LAT, MOTOR_PORT);
    mapActualToRequest(VACUUM_RB_LAT, VACUUM_RB_PORT);
    mapActualToRequest(HV_RB_LAT, HV_RB_PORT);
    mapActualToRequest(SPARE_IO_LAT, SPARE_IO_PORT);
  }

  private void mapActualToRequest(SaintCommandAddress request, SaintCommandAddress actual) {
    for (int i = 0; i < request.getBytes().length; i++) {
      actualToRequest.put((int) actual.getBytes()[i], (int) request.getBytes()[i]);
    }
  }

  @Activate
  void activate(SaintHardwareSimulationConfiguration configuration) throws IOException {
    openPort();
  }

  @Deactivate
  void deactivate() throws IOException {
    closePort();
  }

  private synchronized void openPort() {
    ByteBuffer messageBuffer = ByteBuffer.allocate(SaintController.MESSAGE_SIZE);

    stream = port.openStream(SaintController.MESSAGE_SIZE);
    stream.observe(buffer -> {
      do {
        boolean filled = false;
        do {
          messageBuffer.put(buffer.get());
          filled = !messageBuffer.hasRemaining();
        } while (!filled && buffer.hasRemaining());

        if (filled) {
          messageBuffer.flip();
          receiveMessage(messageBuffer);
          messageBuffer.clear();
        }
      } while (buffer.hasRemaining());
    });
  }

  private synchronized void closePort() throws IOException {
    if (isOpen()) {
      stream.close();
      stream = null;
    }
  }

  private boolean isOpen() {
    return stream != null;
  }

  private void receiveMessage(ByteBuffer messageBuffer) {
    SaintCommandType command = fromByte(messageBuffer.get());
    int address = messageBuffer.get() & 0xFF;
    byte checksum = messageBuffer.get();
    byte data = messageBuffer.get();

    if (actualToRequest.containsKey(address))
      address = actualToRequest.get(address);

    while (address >= memory.size()) {
      memory.add((byte) 0);
    }

    switch (command) {
    case INPUT:
      data = memory.get(address);
      break;

    case OUTPUT:
      memory.set(address, data);
      break;

    default:
    }

    ByteBuffer responseBuffer = ByteBuffer.allocate(4);
    responseBuffer.put((byte) 0);
    responseBuffer.put((byte) 0);
    responseBuffer.put(checksum);
    responseBuffer.put(data);

    try {
      responseBuffer.flip();
      stream.write(responseBuffer);
    } catch (IOException e) {
      log.log(Level.ERROR, new CommsException("Unable to send simulated hardware response", e));
    }
  }
}
