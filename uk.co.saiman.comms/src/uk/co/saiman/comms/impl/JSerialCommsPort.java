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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static com.fazecast.jSerialComm.SerialPort.NO_PARITY;
import static com.fazecast.jSerialComm.SerialPort.ONE_STOP_BIT;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_WRITE_BLOCKING;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsStream;
import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observer;

/**
 * A {@link uk.co.saiman.comms.serial.SerialPort serial port} implementation
 * based on the JSerialComms library.
 * 
 * @author Elias N Vasylenko
 */
public class JSerialCommsPort implements uk.co.saiman.comms.serial.SerialPort {
  private final SerialPort serialPort;

  private CommsChannel openChannel;

  protected JSerialCommsPort(SerialPort serialPort) {
    this.serialPort = serialPort;

    serialPort.setNumDataBits(Byte.SIZE);
    serialPort.setBaudRate(9600);
    serialPort.setNumStopBits(ONE_STOP_BIT);
    serialPort.setParity(NO_PARITY);
    serialPort.setComPortTimeouts(TIMEOUT_READ_BLOCKING | TIMEOUT_WRITE_BLOCKING, 1000, 1000);
  }

  @Override
  public synchronized void close() {
    closeChannel();
  }

  @Override
  public synchronized boolean isOpen() {
    return openChannel != null;
  }

  @Override
  public String getName() {
    return serialPort.getSystemPortName();
  }

  @Override
  public String toString() {
    return serialPort.getDescriptivePortName() + " " + serialPort.getSystemPortName();
  }

  @Override
  public synchronized CommsChannel openChannel() {
    if (serialPort.getSystemPortName().equals("/dev/null")) {
      throw new CommsException("Port is not valid " + this);
    }

    if (isOpen()) {
      throw new CommsException("Port already in use " + this);
    }

    closeChannel();

    if (!serialPort.openPort()) {
      throw new CommsException("Cannot open port " + this);
    }

    HotObservable<CommsChannel> availableObservable = new HotObservable<>();

    setPortListener(new SerialPortDataListener() {
      @Override
      public void serialEvent(SerialPortEvent event) {
        availableObservable.next(openChannel);
      }

      @Override
      public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
      }
    });

    openChannel = new CommsChannel() {
      private boolean open = true;

      @Override
      public int bytesAvailable() {
        assertOpen();
        return serialPort.bytesAvailable();
      }

      @Override
      public int write(ByteBuffer src) throws IOException {
        assertOpen();
        byte[] bytes = new byte[src.remaining()];
        src.get(bytes);
        return serialPort.writeBytes(bytes, bytes.length);
      }

      @Override
      public int read(ByteBuffer dst) throws IOException {
        assertOpen();
        byte[] bytes = new byte[dst.remaining()];
        int read = serialPort.readBytes(bytes, bytes.length);

        if (read < 0)
          throw new CommsException("Unknown port error " + read);

        dst.put(bytes, 0, read);
        return read;
      }

      private void assertOpen() {
        if (!isOpen())
          throw new CommsException("Channel is closed");
      }

      @Override
      public boolean isOpen() {
        return open;
      }

      @Override
      public void close() {
        if (open) {
          open = false;
          closeChannel();
        }
      }

      @Override
      public Disposable observe(Observer<? super CommsChannel> observer) {
        return availableObservable.observe(observer);
      }
    };

    return openChannel;
  }

  @Override
  public synchronized CommsStream openStream(int packetSize) {
    CommsChannel channel = openChannel();
    HotObservable<ByteBuffer> byteObservable = new HotObservable<>();

    if (packetSize <= 0) {
      channel.observe(c -> {
        byte[] bytes = new byte[c.bytesAvailable()];
        serialPort.readBytes(bytes, bytes.length);
        byteObservable.next(ByteBuffer.wrap(bytes));
      });
    } else {
      setPortListener(new SerialPortPacketListener() {
        @Override
        public void serialEvent(SerialPortEvent event) {
          byteObservable.next(ByteBuffer.wrap(event.getReceivedData()));
        }

        @Override
        public int getListeningEvents() {
          return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public int getPacketSize() {
          return packetSize;
        }
      });
    }

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
        return byteObservable.observe(observer);
      }

      @Override
      public void close() throws IOException {
        serialPort.removeDataListener();
        byteObservable.complete();
        channel.close();
      }
    };
  }

  private synchronized void closeChannel() {
    openChannel = null;
    if (!serialPort.closePort()) {
      throw new CommsException("Cannot close port " + this);
    }
  }

  private void setPortListener(SerialPortDataListener listener) {
    serialPort.removeDataListener();
    if (!serialPort.addDataListener(listener)) {
      closeChannel();
      throw new CommsException("Cannot add listener to port " + this);
    }
  }
}
