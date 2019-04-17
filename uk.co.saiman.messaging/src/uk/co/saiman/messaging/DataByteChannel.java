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
 * This file is part of uk.co.saiman.messaging.
 *
 * uk.co.saiman.messaging is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.messaging is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public final class DataByteChannel implements ByteChannel {
  private final DataBuffer reader;
  private final DataSender writer;
  private boolean open = true;

  private DataByteChannel(DataBuffer reader, DataSender writer) {
    this.reader = reader;
    this.writer = writer;
  }

  static DataByteChannel over(DataChannel dataChannel, int bufferSize) throws IOException {
    return new DataByteChannel(dataChannel.openDataBuffer(bufferSize), dataChannel);
  }

  static ReadableByteChannel over(DataReceiver dataReceiver, int bufferSize) throws IOException {
    return new DataByteChannel(dataReceiver.openDataBuffer(bufferSize), null);
  }

  static WritableByteChannel over(DataSender dataSender) {
    return new DataByteChannel(null, dataSender);
  }

  @Override
  public boolean isOpen() {
    return reader.isOpen();
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public int read(ByteBuffer buffer) throws IOException {
    if (!open) {
      throw new ClosedChannelException();
    }
    try {
      return reader.readData(buffer);
    } catch (IOException e) {
      close();
      throw e;
    }
  }

  @Override
  public int write(ByteBuffer buffer) throws IOException {
    if (!open) {
      throw new ClosedChannelException();
    }
    try {
      return writer.sendData(buffer);
    } catch (IOException e) {
      close();
      throw e;
    }
  }
}