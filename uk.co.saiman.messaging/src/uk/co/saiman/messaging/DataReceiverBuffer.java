package uk.co.saiman.messaging;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static uk.co.saiman.observable.Observer.onFailure;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import uk.co.saiman.observable.Disposable;

class DataReceiverBuffer implements DataBuffer {
  private final ByteBuffer buffer;
  private final Queue<Object> readQueue;
  private Disposable observation;
  private Throwable cause;

  public DataReceiverBuffer(DataReceiver receiver, int size) throws IOException {
    buffer = ByteBuffer.allocate(size);
    readQueue = new LinkedList<>();

    observation = receiver
        .receiveData()
        .weakReference(this)
        .then(onFailure(this::fail))
        .observe(o -> o.owner().writeData(o.message()));

    assertOpen();
  }

  private synchronized void writeData(ByteBuffer buffer) {
    while (buffer.remaining() > this.buffer.remaining()) {
      if (readQueue.isEmpty()) {
        throw new BufferOverflowException();
      } else {
        byte[] bytes = new byte[this.buffer.remaining()];
        buffer.get(bytes);
        this.buffer.put(bytes);
        notifyAll();
      }
    }
    this.buffer.put(buffer);
  }

  private synchronized void fail(Throwable cause) {
    close();
    this.cause = cause;
  }

  @Override
  public synchronized boolean isOpen() {
    return observation != null;
  }

  @Override
  public synchronized void close() {
    if (observation != null) {
      observation = null;
      observation.cancel();
    }
  }

  private synchronized void assertOpen() throws IOException {
    if (!isOpen()) {
      if (cause != null) {
        throw new IOException(cause);
      } else {
        throw new ClosedChannelException();
      }
    }
  }

  @Override
  public synchronized int readData(ByteBuffer buffer) throws IOException {
    if (!readQueue.isEmpty()) {
      return 0;
    }
    this.buffer.flip();
    byte[] bytes = new byte[Math.min(this.buffer.remaining(), buffer.remaining())];
    this.buffer.get(bytes);
    buffer.put(bytes);
    this.buffer.compact();
    return bytes.length;
  }

  @Override
  public synchronized void readData(ByteBuffer buffer, TimeUnit timeUnit, long timeout)
      throws IOException {
    if (buffer.remaining() > this.buffer.capacity()) {
      throw new BufferUnderflowException();
    }

    if (buffer.remaining() > this.buffer.position()) {
      var token = new Object();
      readQueue.offer(token);

      timeout = MILLISECONDS.convert(timeout, timeUnit);
      long startTime = System.currentTimeMillis();
      do {
        assertOpen();
        long delay = timeout + startTime - System.currentTimeMillis();
        if (delay <= 0) {
          throw new InterruptedByTimeoutException();
        }
        try {
          wait(delay);
        } catch (InterruptedException e) {
          throw new InterruptedIOException();
        }
      } while (readQueue.element() != token || buffer.remaining() > this.buffer.position());

      readQueue.poll();
    }

    this.buffer.flip();
    buffer.put(this.buffer);
    this.buffer.compact();
  }

  @Override
  public synchronized void flush() {
    buffer.clear();
  }

  @Override
  public synchronized int availableBytes() {
    return buffer.position();
  }
}
