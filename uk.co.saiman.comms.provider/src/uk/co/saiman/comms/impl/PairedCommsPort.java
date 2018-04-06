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
 * This file is part of uk.co.saiman.comms.provider.
 *
 * uk.co.saiman.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static java.lang.Math.min;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observer;

public class PairedCommsPort implements CommsPort {
  private static final int BUFFER_SIZE = 1024;

  private final PairedCommsPort partner;
  private final String name;
  private final ByteBuffer buffer;
  private final HotObservable<CommsChannel> updated;
  private CommsChannel channel;

  public PairedCommsPort(String name, String partnerName) {
    this.partner = new PairedCommsPort(partnerName, this);
    this.name = name;
    this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
    this.updated = new HotObservable<>();
    updated.complete();
  }

  private PairedCommsPort(String name, PairedCommsPort partner) {
    this.partner = partner;
    this.name = name;
    this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
    this.updated = new HotObservable<>();
    updated.complete();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public synchronized boolean isOpen() {
    return channel != null;
  }

  @Override
  public synchronized void kill() {
    if (channel != null) {
      channel.close();
    }
  }

  @Override
  public synchronized CommsChannel openChannel() {
    if (isOpen()) {
      throw new CommsException("Port already in use " + this);
    }

    updated.start();
    return channel = new CommsChannel() {
      private boolean disposed;

      private void checkState() {
        if (disposed)
          throw new CommsException("Port is disposed");
      }

      @Override
      public Disposable observe(Observer<? super CommsChannel> observer) {
        checkState();
        return updated.observe(observer);
      }

      @Override
      public int write(ByteBuffer src) throws IOException {
        checkState();
        return partner.input(src);
      }

      @Override
      public boolean isOpen() {
        return !disposed;
      }

      @Override
      public int read(ByteBuffer dst) throws IOException {
        checkState();
        synchronized (buffer) {
          if (buffer.position() < dst.remaining()) {
            Disposable disposable = updated.observe(o -> {
              synchronized (buffer) {
                if (buffer.position() >= dst.remaining())
                  buffer.notifyAll();
              }
            });
            try {
              buffer.wait(1000);
            } catch (InterruptedException e) {}
            disposable.cancel();
          }

          try {
            buffer.flip();
            int read = min(dst.remaining(), buffer.remaining());
            byte[] bytes = new byte[read];
            buffer.get(bytes);
            dst.put(bytes, 0, read);
            return read;
          } finally {
            buffer.compact();
          }
        }
      }

      @Override
      public void close() {
        synchronized (buffer) {
          channel = null;
          disposed = true;
          updated.complete();
        }
      }

      @Override
      public int bytesAvailable() {
        synchronized (buffer) {
          // position() not remaining() because we are writing
          return buffer.position();
        }
      }
    };
  }

  private int input(ByteBuffer inputBuffer) {
    synchronized (buffer) {
      if (inputBuffer.remaining() > buffer.remaining()) {
        buffer.compact();
      }

      int written = Math.min(inputBuffer.remaining(), buffer.remaining());
      byte[] bytes = new byte[written];
      inputBuffer.get(bytes);
      buffer.put(bytes, 0, written);

      if (channel != null)
        updated.next(channel);

      return written;
    }
  }

  @Override
  public CommsStream openStream(int packetSize) {
    channel = openChannel();

    return new CommsStream() {
      @Override
      public boolean isOpen() {
        return channel.isOpen();
      }

      @Override
      public int write(ByteBuffer src) throws IOException {
        return channel.write(src);
      }

      @Override
      public Disposable observe(Observer<? super ByteBuffer> observer) {
        return channel.map(c -> {
          try {
            return c.read();
          } catch (IOException e) {
            throw new CommsException("Cannot read from " + c, e);
          }
        }).observe(observer);
      }

      @Override
      public void close() throws IOException {
        channel.close();
      }
    };
  }

  public PairedCommsPort getPartner() {
    return partner;
  }

  public List<CommsPort> asList() {
    return Arrays.asList(this, getPartner());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getName();
  }
}
