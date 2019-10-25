package uk.co.saiman.bytes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.Queue;

public class TransferBuffer {
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  public static TransferBuffer openBuffer() throws IOException {
    return openBuffer(DEFAULT_BUFFER_SIZE);
  }

  public static TransferBuffer openBuffer(int bufferSize) throws IOException {
    return new TransferBuffer(bufferSize);
  }

  private ReadableByteChannel read;

  private Queue<ByteBuffer> buffers;
  private ByteBuffer head;

  private final int bufferSize;

  public TransferBuffer(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  private int copy(ByteBuffer from, ByteBuffer to) {
    int size;
    if (from.remaining() <= to.remaining()) {
      size = from.remaining();
      to.put(from);

    } else {
      size = to.remaining();
      var bytes = new byte[size];
      from.get(bytes);
      to.put(bytes);
    }
    return size;
  }

  public synchronized WritableByteChannel openWritableChannel() throws IOException {
    if (buffers != null) {
      throw new ClosedChannelException();
    }
    buffers = new ArrayDeque<>();
    return new WritableByteChannel() {
      private boolean open = true;

      @Override
      public boolean isOpen() {
        return open;
      }

      @Override
      public void close() throws IOException {
        synchronized (TransferBuffer.this) {
          open = false;
          head = null;
        }
      }

      @Override
      public int write(ByteBuffer from) throws IOException {
        synchronized (TransferBuffer.this) {
          if (head == null || !head.hasRemaining()) {
            head = ByteBuffer.allocate(bufferSize);
            buffers.offer(head);
          }

          return copy(from, head);
        }
      }
    };
  }

  public synchronized ReadableByteChannel openReadableChannel() throws IOException {
    if (read != null || buffers == null) {
      throw new ClosedChannelException();
    }
    return read = new ReadableByteChannel() {
      private boolean open = true;

      @Override
      public boolean isOpen() {
        return open;
      }

      @Override
      public void close() throws IOException {
        synchronized (TransferBuffer.this) {
          open = false;
          buffers = null;
        }
      }

      @Override
      public int read(ByteBuffer to) throws IOException {
        synchronized (TransferBuffer.this) {
          var buffer = buffers.peek();
          if (buffer != head && buffer.position() == 0) {
            buffers.poll();
            buffer = buffers.peek();
          }
          if (buffer == null) {
            return -1;
          }

          buffer.flip();

          int size = copy(buffer, to);

          buffer.compact();

          return size;
        }
      }
    };
  }
}
