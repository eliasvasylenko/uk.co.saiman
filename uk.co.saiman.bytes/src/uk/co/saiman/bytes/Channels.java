package uk.co.saiman.bytes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class Channels {
  private static final int BUFFER_SIZE = 2048;

  public void pipe(ReadableByteChannel from, WritableByteChannel to) throws IOException {
    final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    while (from.read(buffer) != -1) {
      buffer.flip();
      to.write(buffer);
      buffer.compact();
    }
    buffer.flip();
    while (buffer.hasRemaining()) {
      to.write(buffer);
    }
  }
}
