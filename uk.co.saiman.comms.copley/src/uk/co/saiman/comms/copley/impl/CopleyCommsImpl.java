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
package uk.co.saiman.comms.copley.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MANDATORY;
import static uk.co.saiman.comms.copley.CopleyOperationID.NO_OP;
import static uk.co.saiman.comms.copley.ErrorCode.SUCCESS;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.ByteConverters;
import uk.co.saiman.comms.Comms;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.SimpleComms;
import uk.co.saiman.comms.SimpleController;
import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyOperationID;
import uk.co.saiman.comms.copley.ErrorCode;
import uk.co.saiman.comms.copley.impl.CopleyCommsImpl.CopleyCommsConfiguration;

@Designate(ocd = CopleyCommsConfiguration.class, factory = true)
@Component(
    name = CopleyCommsImpl.CONFIGURATION_PID,
    configurationPid = CopleyCommsImpl.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyCommsImpl extends SimpleComms<CopleyController>
    implements CopleyComms, Comms<CopleyController> {
  static final String CONFIGURATION_PID = "uk.co.saiman.comms.copley";

  public static final int NODE_ID_MASK = 0x7F;
  public static final int NODE_ID_MARK = 0x80;
  public static final byte CHECKSUM = 0x5A;
  public static final int WORD_SIZE = 2;

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "Copley Comms Configuration",
      description = "The configuration for the underlying serial comms for a Copley motor control")
  public @interface CopleyCommsConfiguration {
    @AttributeDefinition(name = "Serial Port", description = "The serial port for comms")
    String port_target();

    @AttributeDefinition(
        name = "Node Count",
        description = "The node count for multi-drop mode dispatch, or 0 for the directly connected node")
    int nodeCount() default 0;
  }

  @Reference
  CommsPort port;

  private int nodeCount;
  private boolean nodeIDValid;

  @Reference
  ByteConverters converters;

  @Activate
  void activate(CopleyCommsConfiguration configuration) throws IOException {
    configure(configuration);
    setComms(port);
  }

  @Modified
  void configure(CopleyCommsConfiguration configuration) throws IOException {
    nodeIDValid = (configuration.nodeCount() & NODE_ID_MASK) == configuration.nodeCount();
    nodeCount = configuration.nodeCount();
    checkNodeId();
  }

  private boolean checkNodeId() {
    if (!nodeIDValid)
      setFault(new CommsException("Invalid node id number " + nodeCount));

    return nodeIDValid;
  }

  @Deactivate
  void deactivate() throws IOException {
    unsetComms();
  }

  @Override
  protected SimpleController<CopleyController> createController() {
    CopleyControllerImpl controller = new CopleyControllerImpl(this);

    return new SimpleController<CopleyController>() {
      @Override
      public CopleyController getController() {
        return controller;
      }

      @Override
      public void closeController() {
        controller.close();
      }
    };
  }

  ByteConverters getConverters() {
    return converters;
  }

  @Override
  protected void checkComms() {
    if (checkNodeId())
      ping();
  }

  private void ping() {
    executeCopleyCommand(NO_OP, new byte[] {});
  }

  byte[] executeCopleyCommand(CopleyOperationID operation, byte[] output) {
    return useChannel(channel -> {
      sendCopleyCommand(operation, channel, output);
      return receiveCopleyCommand(channel);
    });
  }

  private void sendCopleyCommand(CopleyOperationID operation, ByteChannel channel, byte[] output) {
    byte id = (byte) (nodeCount == 0 ? nodeCount : (nodeCount | NODE_ID_MARK));
    byte size = (byte) (output.length / WORD_SIZE);
    byte opCode = operation.getCode();
    byte checksum = (byte) (CHECKSUM ^ id ^ size ^ opCode);
    for (byte outputByte : output)
      checksum ^= outputByte;

    ByteBuffer message_buffer = ByteBuffer.allocate(HEADER_SIZE + output.length);
    message_buffer.put(id);
    message_buffer.put(checksum);
    message_buffer.put(size);
    message_buffer.put(opCode);
    message_buffer.put(output);

    try {
      message_buffer.flip();
      channel.write(message_buffer);
    } catch (IOException e) {
      throw setFault(new CommsException("Problem dispatching command"));
    }
  }

  private byte[] receiveCopleyCommand(ByteChannel channel) {
    ByteBuffer message_buffer = ByteBuffer.allocate(HEADER_SIZE);
    try {
      if (channel.read(message_buffer) != message_buffer.limit()) {
        throw setFault(new CommsException("Response too short " + message_buffer.limit()));
      }
      message_buffer.flip();
    } catch (IOException e) {
      throw setFault(new CommsException("Problem receiving command response"));
    }

    message_buffer.get(); // reserved
    byte checksum = message_buffer.get();
    int size = message_buffer.get() * WORD_SIZE;
    ErrorCode errorCode = ErrorCode.values()[message_buffer.get()];

    message_buffer = ByteBuffer.allocate(size);
    try {
      if (channel.read(message_buffer) != message_buffer.limit()) {
        throw setFault(new CommsException("Response too short " + message_buffer.limit()));
      }
      message_buffer.flip();
    } catch (IOException e) {
      throw setFault(new CommsException("Problem receiving command response"));
    }

    if (errorCode != SUCCESS) {
      throw new CopleyErrorException(errorCode);
    }

    byte[] input = new byte[size];
    message_buffer.get(input);

    return input;
  }
}
