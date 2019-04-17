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