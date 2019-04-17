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
 * This file is part of uk.co.saiman.comms.copley.provider.
 *
 * uk.co.saiman.comms.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import static java.util.concurrent.TimeUnit.SECONDS;
import static uk.co.saiman.comms.copley.CopleyController.CHECKSUM;
import static uk.co.saiman.comms.copley.CopleyController.HEADER_SIZE;
import static uk.co.saiman.comms.copley.CopleyController.WORD_SIZE;
import static uk.co.saiman.comms.copley.CopleyOperationID.GET_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperationID.NO_OP;
import static uk.co.saiman.comms.copley.CopleyVariableID.DRIVE_EVENT_STATUS;
import static uk.co.saiman.comms.copley.ErrorCode.ILLEGAL_AXIS_NUMBER;
import static uk.co.saiman.comms.copley.ErrorCode.SUCCESS;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyNode;
import uk.co.saiman.comms.copley.CopleyOperationID;
import uk.co.saiman.comms.copley.ErrorCode;
import uk.co.saiman.comms.copley.OperatingMode;
import uk.co.saiman.comms.copley.VariableIdentifier;
import uk.co.saiman.messaging.DataBuffer;

public class CopleyNodeImpl implements CopleyNode {
  private static final int MAXIMUM_AXES = 8;

  public static final int NODE_ID_MASK = 0x7F;
  public static final int NODE_ID_MARK = 0x80;

  private final int nodeId;
  private final int nodeIndex;

  private CopleyControllerImpl comms;

  private final List<CopleyAxis> axes;

  public CopleyNodeImpl(CopleyControllerImpl comms) throws IOException {
    this(comms, (byte) 0, 0);
  }

  public CopleyNodeImpl(CopleyControllerImpl comms, int nodeIndex) throws IOException {
    this(comms, (byte) (nodeIndex | NODE_ID_MARK), nodeIndex);
  }

  protected CopleyNodeImpl(CopleyControllerImpl comms, int nodeId, int nodeIndex)
      throws IOException {
    this.comms = comms;
    this.nodeId = nodeId;
    this.nodeIndex = nodeIndex;
    int axisCount = countAxes();
    this.axes = new ArrayList<>(axisCount);
    for (int i = 0; i < axisCount; i++) {
      axes.add(new CopleyAxisImpl(this, i));
    }
  }

  private int countAxes() throws IOException {
    int axes = 0;
    do {
      try {
        executeCopleyCommand(
            GET_VARIABLE,
            getConverter(VariableIdentifier.class)
                .toBytes(new VariableIdentifier(DRIVE_EVENT_STATUS, axes, ACTIVE)));
        axes++;
      } catch (CopleyErrorException e) {
        if (e.getCode() == ILLEGAL_AXIS_NUMBER) {
          break;
        } else {
          throw e;
        }
      }
    } while (axes < MAXIMUM_AXES);
    return axes;
  }

  @Override
  public int getId() {
    return nodeId;
  }

  @Override
  public OperatingMode getOperatingMode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setOperatingMode(OperatingMode mode) {
    // TODO Auto-generated method stub

  }

  @Override
  public Stream<CopleyAxis> getAxes() {
    return axes.stream();
  }

  <T> ByteConverter<T> getConverter(Class<T> type) {
    return comms.getConverters().getConverter(type);
  }

  protected void ping() throws IOException {
    executeCopleyCommand(NO_OP, new byte[] {});
  }

  byte[] executeCopleyCommand(CopleyOperationID operation, byte[] output) throws IOException {
    try (DataBuffer buffer = comms.getReceiver().openDataBuffer(1024)) {
      sendCopleyCommand(operation, output);
      return receiveCopleyCommand(buffer);
    }
  }

  private void sendCopleyCommand(CopleyOperationID operation, byte[] output) throws IOException {
    byte id = (byte) nodeId;
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

    message_buffer.flip();
    comms.getSender().sendData(message_buffer);
  }

  private byte[] receiveCopleyCommand(DataBuffer buffer) throws IOException {
    ByteBuffer message_buffer = ByteBuffer.allocate(HEADER_SIZE);
    buffer.readData(message_buffer, SECONDS, 2);
    message_buffer.flip();

    message_buffer.get(); // reserved
    byte checksum = message_buffer.get();
    int size = message_buffer.get() * WORD_SIZE;
    ErrorCode errorCode = ErrorCode.values()[message_buffer.get()];

    message_buffer = ByteBuffer.allocate(size);
    buffer.readData(message_buffer, SECONDS, 2);
    message_buffer.flip();

    if (errorCode != SUCCESS) {
      throw new CopleyErrorException(errorCode);
    }

    byte[] input = new byte[size];
    message_buffer.get(input);

    return input;
  }
}
