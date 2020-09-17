/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
      observation.cancel();
      observation = null;
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
        long delay = timeout - System.currentTimeMillis() + startTime;
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
    byte[] bytes = new byte[buffer.remaining()];
    this.buffer.get(bytes, 0, bytes.length);
    buffer.put(bytes);
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
