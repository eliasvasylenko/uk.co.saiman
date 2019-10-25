/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.copley.provider.
 *
 * uk.co.saiman.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.simulation;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.comms.copley.CopleyController.HEADER_SIZE;
import static uk.co.saiman.comms.copley.CopleyController.WORD_SIZE;
import static uk.co.saiman.comms.copley.CopleyVariableID.ACTUAL_POSITION;
import static uk.co.saiman.comms.copley.CopleyVariableID.AMPLIFIER_STATE;
import static uk.co.saiman.comms.copley.CopleyVariableID.DRIVE_EVENT_STATUS;
import static uk.co.saiman.comms.copley.CopleyVariableID.LATCHED_EVENT_STATUS;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_ANGULAR_RESOLUTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_DIRECTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_LINEAR_RESOLUTION;
import static uk.co.saiman.comms.copley.CopleyVariableID.MOTOR_ENCODER_UNITS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_POSITION_COUNTS;
import static uk.co.saiman.comms.copley.CopleyVariableID.TRAJECTORY_PROFILE_MODE;
import static uk.co.saiman.comms.copley.ErrorCode.ILLEGAL_AXIS_NUMBER;
import static uk.co.saiman.comms.copley.ErrorCode.ILLEGAL_OP_CODE;
import static uk.co.saiman.comms.copley.ErrorCode.INVALID_NODE_ID;
import static uk.co.saiman.comms.copley.impl.CopleyNodeImpl.NODE_ID_MASK;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.comms.copley.CommandHeader;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.EventStatusRegister;
import uk.co.saiman.comms.copley.Int32;
import uk.co.saiman.comms.copley.ResponseHeader;
import uk.co.saiman.comms.copley.VariableIdentifier;
import uk.co.saiman.comms.copley.impl.CopleyErrorException;
import uk.co.saiman.comms.copley.simulation.CopleyHardwareSimulation.CopleyHardwareSimulationConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.messaging.DataBuffer;
import uk.co.saiman.messaging.DataReceiver;
import uk.co.saiman.messaging.DataSender;

@Designate(ocd = CopleyHardwareSimulationConfiguration.class, factory = true)
@Component(
    name = CopleyHardwareSimulation.CONFIGURATION_PID,
    configurationPid = CopleyHardwareSimulation.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyHardwareSimulation {
  static final String CONFIGURATION_PID = "uk.co.saiman.comms.copley.simulation";

  private static final double MOTOR_SPEED_UNITS_PER_MILLISECOND = 10;

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "Copley Comms Hardware Simulation Configuration",
      description = "A configuration for a simulation of the Copley motor control interface")
  public @interface CopleyHardwareSimulationConfiguration {
    @AttributeDefinition(
        name = "Serial Port",
        description = "The serial port for the hardware simulation")
    String port_target();

    @AttributeDefinition(
        name = "Node Count",
        description = "The number of nodes for multi-drop mode dispatch, or 0 for one direct connection")
    int nodes()

    default 0;

    @AttributeDefinition(
        name = "Axis Count",
        description = "The number of axes supported by the drive")
    int axes() default 1;
  }

  private final Log log;

  private final ByteConverterService converters;

  private final DataSender response;
  private final DataReceiver command;

  private Thread observation;

  private final int nodes;
  private final int axes;

  private final Map<CopleyVariableID, SimulatedVariable> variables = new HashMap<>();

  private final ReferenceVariable<Int32> requestedPosition;
  private final InterpolatedVariable<Int32> actualPosition;
  private final ComputedVariable<EventStatusRegister> eventStatus;

  @Activate
  public CopleyHardwareSimulation(
      CopleyHardwareSimulationConfiguration configuration,
      @Reference Log log,
      @Reference ByteConverterService converters,
      @Reference(name = "response") DataSender response,
      @Reference(name = "command") DataReceiver command) throws IOException {
    /*
     * services
     */
    this.log = log;
    this.converters = converters;
    this.response = response;
    this.command = command;

    /*
     * configuration
     */
    this.nodes = configuration.nodes();
    this.axes = configuration.axes();
    this.variables.clear();

    /*
     * variables
     */
    this.requestedPosition = new ReferenceVariable<>(
        axes,
        converters.getConverter(Int32.class),
        new Int32(0));
    this.actualPosition = new InterpolatedVariable<>(
        axes,
        requestedPosition,
        MOTOR_SPEED_UNITS_PER_MILLISECOND,
        i -> (double) i.value,
        d -> new Int32(d.intValue()));
    this.eventStatus = new ComputedVariable<>(converters.getConverter(EventStatusRegister.class)) {
      @Override
      public EventStatusRegister compute(int axis) {
        var status = new EventStatusRegister();
        status.motionActive = actualPosition.isMoving(axis);
        return status;
      }
    };

    variables.put(DRIVE_EVENT_STATUS, eventStatus);
    variables.put(LATCHED_EVENT_STATUS, new ByteVariable(axes, 2));
    variables.put(AMPLIFIER_STATE, new ByteVariable(axes, 1));
    variables.put(TRAJECTORY_PROFILE_MODE, new ByteVariable(axes, 1));

    variables.put(TRAJECTORY_POSITION_COUNTS, requestedPosition);
    variables.put(ACTUAL_POSITION, actualPosition);

    variables.put(MOTOR_ENCODER_UNITS, new ByteVariable(axes, 1));
    variables.put(MOTOR_ENCODER_ANGULAR_RESOLUTION, new ByteVariable(axes, 2));
    variables.put(MOTOR_ENCODER_LINEAR_RESOLUTION, new ByteVariable(axes, 1));
    variables.put(MOTOR_ENCODER_DIRECTION, new ByteVariable(axes, 1));

    openObservation();
  }

  @Deactivate
  void deactivate() throws IOException {
    closeObservation();
  }

  private synchronized void openObservation() {
    if (observation == null) {
      observation = new Thread(() -> {
        try (DataBuffer buffer = command.openDataBuffer(2048)) {
          for (;;) {
            var header = readHeader(buffer);
            readMessage(buffer, header);
          }
        } catch (Exception e) {
          if (observation != null) {
            log.log(ERROR, e);
          }
        }
      });
      observation.start();
    }
  }

  private synchronized void closeObservation() {
    if (observation != null) {
      var observation = this.observation;
      this.observation = null;
      observation.interrupt();
    }
  }

  private CommandHeader readHeader(DataBuffer buffer) throws IOException {
    ByteBuffer headerBytes = ByteBuffer.allocate(HEADER_SIZE);
    buffer.readData(headerBytes, TimeUnit.MILLISECONDS, Long.MAX_VALUE);
    headerBytes.flip();

    var header = converters.getConverter(CommandHeader.class).toObject(headerBytes.array());

    return header;
  }

  private void readMessage(DataBuffer buffer, CommandHeader commandHeader) throws IOException {
    ByteBuffer message = ByteBuffer.allocate(commandHeader.messageBytes());
    buffer.readData(message, TimeUnit.MILLISECONDS, Long.MAX_VALUE);
    message.flip();

    try {
      if (nodes != (commandHeader.currentNode & NODE_ID_MASK)) {
        throw new CopleyErrorException(INVALID_NODE_ID);
      }

      byte[] result = new byte[] {};

      VariableIdentifier variable;
      CopleyVariableID id;
      switch (commandHeader.operation()) {
      case GET_VARIABLE:
        variable = getVariableIdentifier(message);
        id = CopleyVariableID.forCode(variable.variableID);
        if (variable.axis < 0 || variable.axis >= axes) {
          throw new CopleyErrorException(ILLEGAL_AXIS_NUMBER);
        } else {
          result = getVariable(id).get(variable.axis, variable.bank());
        }
        break;
      case SET_VARIABLE:
        variable = getVariableIdentifier(message);
        id = CopleyVariableID.forCode(variable.variableID);
        byte[] value = new byte[message.remaining()];
        message.get(value);
        if (variable.axis < 0 || variable.axis >= axes) {
          throw new CopleyErrorException(ILLEGAL_AXIS_NUMBER);
        } else {
          getVariable(id).set(variable.axis, variable.bank(), value);
        }
        break;
      case COPY_VARIABLE:
        variable = getVariableIdentifier(message);
        id = CopleyVariableID.forCode(variable.variableID);
        if (variable.axis < 0 || variable.axis >= axes) {
          throw new CopleyErrorException(ILLEGAL_AXIS_NUMBER);
        } else {
          getVariable(id).copy(variable.axis, variable.bank());
        }
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
        throw new CopleyErrorException(ILLEGAL_OP_CODE);
      }

      try {
        var responseHeader = new ResponseHeader(result);
        var headerBytes = converters.getConverter(ResponseHeader.class).toBytes(responseHeader);

        ByteBuffer response = ByteBuffer.allocate(headerBytes.length + result.length);
        response.put(headerBytes);
        response.put(result);
        response.flip();
        this.response.sendData(response);
      } catch (Exception e) {
        log.log(ERROR, "Unable to send simulated hardware success response: " + e.getMessage(), e);
      }

    } catch (CopleyErrorException e) {
      try {
        var responseHeader = new ResponseHeader(e.getCode());
        var headerBytes = converters.getConverter(ResponseHeader.class).toBytes(responseHeader);

        ByteBuffer response = ByteBuffer.allocate(headerBytes.length);
        response.put(headerBytes);
        response.flip();
        this.response.sendData(response);
      } catch (Exception e2) {
        e2.addSuppressed(e);
        log.log(ERROR, "Unable to send simulated hardware error response: " + e2.getMessage(), e2);
      }

    } catch (Exception e) {
      log.log(ERROR, "Unable to simulate hardware response: " + e.getMessage(), e);
    }
  }

  private SimulatedVariable getVariable(CopleyVariableID id) {
    if (!variables.containsKey(id))
      throw new IllegalArgumentException();
    return variables.get(id);
  }

  private VariableIdentifier getVariableIdentifier(ByteBuffer message) {
    byte[] bytes = new byte[WORD_SIZE];
    message.get(bytes);
    return converters.getConverter(VariableIdentifier.class).toObject(bytes);
  }
}
