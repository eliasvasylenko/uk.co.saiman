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
