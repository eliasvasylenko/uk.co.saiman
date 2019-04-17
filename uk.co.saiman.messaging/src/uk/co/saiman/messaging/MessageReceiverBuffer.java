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

class MessageReceiverBuffer implements MessageBuffer {
  private final Queue<ByteBuffer> buffer;
  private final int capacity;
  private final Queue<Object> readQueue;
  private Disposable observation;
  private Throwable cause;

  public MessageReceiverBuffer(MessageReceiver receiver, int size) throws IOException {
    buffer = new LinkedList<>();
    capacity = size;
    readQueue = new LinkedList<>();

    observation = receiver
        .receiveMessages()
        .weakReference(this)
        .then(onFailure(this::fail))
        .observe(o -> o.owner().writeMessage(o.message()));

    assertOpen();
  }

  private synchronized void writeMessage(ByteBuffer buffer) {
    if (this.buffer.size() == capacity) {
      throw new BufferOverflowException();
    }
    this.buffer.offer(buffer);
    notifyAll();
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
  public synchronized ByteBuffer readMessage() throws IOException {
    if (!readQueue.isEmpty()) {
      throw new BufferUnderflowException();
    }
    return buffer.poll();
  }

  @Override
  public synchronized ByteBuffer readMessage(TimeUnit timeUnit, long timeout) throws IOException {
    if (buffer.isEmpty()) {
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
      } while (buffer.isEmpty() || readQueue.element() != token);

      readQueue.poll();
    }

    return buffer.poll();
  }

  @Override
  public synchronized void flush() {
    buffer.clear();
  }

  @Override
  public synchronized int availableMessages() {
    return buffer.size();
  }
}
