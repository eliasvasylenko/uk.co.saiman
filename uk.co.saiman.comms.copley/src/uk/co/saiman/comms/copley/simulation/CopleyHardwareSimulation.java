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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.simulation;

import static java.util.Optional.of;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicy.STATIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.comms.copley.CopleyComms.HEADER_SIZE;
import static uk.co.saiman.comms.copley.CopleyVariableID.ACTUAL_POSITION;
import static uk.co.saiman.comms.copley.CopleyVariableID.AMPLIFIER_STATE;
import static uk.co.saiman.comms.copley.CopleyVariableID.DRIVE_EVENT_STATUS;
import static uk.co.saiman.comms.copley.CopleyVariableID.LATCHED_EVENT_STATUS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_POSITION_COUNTS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_PROFILE_MODE;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;
import static uk.co.saiman.comms.copley.VariableBank.STORED;
import static uk.co.saiman.comms.copley.impl.CopleyCommsImpl.NODE_ID_MASK;
import static uk.co.saiman.comms.copley.impl.CopleyCommsImpl.WORD_SIZE;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.ByteConverters;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.comms.copley.CopleyOperationID;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.comms.copley.VariableIdentifier;
import uk.co.saiman.comms.copley.impl.CopleyCommsImpl;
import uk.co.saiman.comms.copley.simulation.CopleyHardwareSimulation.CopleyHardwareSimulationConfiguration;
import uk.co.saiman.comms.serial.SerialPort;
import uk.co.saiman.comms.serial.SerialPorts;
import uk.co.saiman.log.Log;

@Designate(ocd = CopleyHardwareSimulationConfiguration.class, factory = true)
@Component(
    name = CopleyHardwareSimulation.CONFIGURATION_PID,
    configurationPid = CopleyHardwareSimulation.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyHardwareSimulation {
  static final String CONFIGURATION_PID = "uk.co.saiman.comms.copley.simulation";

  private static final double MOTOR_SPEED_UNITS_PER_MILLISECOND = 10;

  @Reference(cardinality = OPTIONAL, policy = DYNAMIC)
  volatile Log log;

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "Copley Comms Hardware Simulation Configuration",
      description = "A configuration for a simulation of the Copley motor control interface")
  public @interface CopleyHardwareSimulationConfiguration {
    @AttributeDefinition(
        name = "Serial Port",
        description = "The serial port for the hardware simulation")
    String serialPort();

    @AttributeDefinition(
        name = "Node Number",
        description = "The node number for multi-drop mode dispatch, or 0 for the directly connected node")
    int node()

    default 0;

    @AttributeDefinition(
        name = "Axis Count",
        description = "The number of axes supported by the drive")
    int axes() default 1;
  }

  @Reference
  ByteConverters converters;

  @Reference(policy = STATIC, policyOption = GREEDY)
  SerialPorts serialPorts;
  private SerialPort port;
  private CommsStream stream;

  private final ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
  private byte currentNode;
  private byte checksum;
  private CopleyOperationID operation;
  private ByteBuffer message;

  private int node;
  private int axes;

  private Map<CopleyVariableID, SimulatedVariable> variables = new HashMap<>();

  private ReferenceVariable<Int32> requestedPosition;

  @Activate
  void activate(CopleyHardwareSimulationConfiguration configuration) throws IOException {
    configure(configuration);
  }

  @Modified
  void configure(CopleyHardwareSimulationConfiguration configuration) throws IOException {
    setPort(configuration.serialPort());
    node = configuration.node();
    axes = configuration.axes();
    variables.clear();

    variables.put(DRIVE_EVENT_STATUS, new ByteVariable(axes, 2));
    variables.put(LATCHED_EVENT_STATUS, new ByteVariable(axes, 2));
    variables.put(AMPLIFIER_STATE, new ByteVariable(axes, 1));
    variables.put(TRAJECTORY_PROFILE_MODE, new ByteVariable(axes, 1));

    requestedPosition = new ReferenceVariable<>(axes, converters.getConverter(Int32.class));
    variables.put(TRAJECTORY_POSITION_COUNTS, requestedPosition);
    variables.put(
        ACTUAL_POSITION,
        new InterpolatedVariable<>(
            axes,
            requestedPosition,
            MOTOR_SPEED_UNITS_PER_MILLISECOND,
            i -> (double) i.value,
            d -> new Int32(d.intValue())));
  }

  @Deactivate
  void deactivate() throws IOException {
    closePort();
  }

  private synchronized void setPort(String serialPort) throws IOException {
    closePort();
    port = serialPorts.getPort(serialPort);
    openPort();
  }

  private synchronized void openPort() {
    stream = port.openStream();
    stream.observe(buffer -> {
      do {
        boolean onHeader = message == null;
        ByteBuffer currentBuffer = onHeader ? header : message;

        do {
          currentBuffer.put(buffer.get());
        } while (currentBuffer.hasRemaining() && buffer.hasRemaining());

        if (!currentBuffer.hasRemaining()) {
          currentBuffer.flip();

          if (onHeader) {
            receiveHeader();
            if (message.capacity() == 0)
              receiveMessage();
          } else {
            receiveMessage();
          }
        }
      } while (buffer.hasRemaining());
    });
  }

  private synchronized void closePort() throws IOException {
    if (stream != null) {
      stream.close();
      stream = null;
    }
  }

  private void receiveHeader() {
    currentNode = (byte) (header.get() & NODE_ID_MASK);
    checksum = header.get();
    message = ByteBuffer.allocate(header.get() * WORD_SIZE);
    operation = CopleyOperationID.getCanonicalOperation(header.get());
    header.clear();
  }

  private void receiveMessage() {
    byte[] result = new byte[] {};

    if (node == currentNode) {
      try {
        VariableIdentifier variable;
        CopleyVariableID id;
        switch (operation) {
        case GET_VARIABLE:
          variable = getVariableIdentifier();
          id = CopleyVariableID.forCode(variable.variableID);
          result = getVariable(id).get(variable.axis, variable.bank ? STORED : ACTIVE);
          break;
        case SET_VARIABLE:
          variable = getVariableIdentifier();
          id = CopleyVariableID.forCode(variable.variableID);

          byte[] value = new byte[message.remaining()];
          message.get(value);
          getVariable(id).set(variable.axis, variable.bank ? STORED : ACTIVE, value);
          break;
        case COPY_VARIABLE:
          variable = getVariableIdentifier();
          id = CopleyVariableID.forCode(variable.variableID);
          getVariable(id).copy(variable.axis, variable.bank ? STORED : ACTIVE);
          break;
        case COPLEY_VIRTUAL_MACHINE:
          break;
        case DYNAMIC_FILE_INTERFACE:
          break;
        case ENCODER:
          break;
        case ERROR_LOG:
          break;
        case GET_CAN_OBJECT:
          break;
        case GET_FLASH_CRC:
          break;
        case GET_OPERATING_MODE:
          break;
        case NO_OP:
          break;
        case RESET:
          break;
        case SET_CAN_OBJECT:
          break;
        case SWITCH_OPERATING_MODE:
          break;
        case TRACE_VARIABLE:
          break;
        case TRAJECTORY:
          break;
        default:
          throw new IllegalArgumentException("Unexpected operation " + operation);
        }

        byte checksum = (byte) (CopleyCommsImpl.CHECKSUM ^ result.length);
        for (byte item : result)
          checksum ^= item;

        ByteBuffer response = ByteBuffer.allocate(result.length + HEADER_SIZE);
        response.put((byte) 0);
        response.put(checksum);
        response.put((byte) (result.length / WORD_SIZE));
        response.put((byte) 0);
        response.put(result);
        response.flip();

        stream.write(response);
      } catch (Exception e) {
        of(log).ifPresent(
            l -> l.log(
                ERROR,
                new CommsException(
                    "Unable to send simulated hardware response: " + e.getMessage(),
                    e)));
      }
    }

    message = null;
  }

  private SimulatedVariable getVariable(CopleyVariableID id) {
    if (!variables.containsKey(id))
      throw new IllegalArgumentException();
    return variables.get(id);
  }

  private VariableIdentifier getVariableIdentifier() {
    byte[] bytes = new byte[WORD_SIZE];
    message.get(bytes);
    return converters.getConverter(VariableIdentifier.class).fromBytes(bytes);
  }
}
