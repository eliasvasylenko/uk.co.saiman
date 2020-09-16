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
 * This file is part of uk.co.saiman.messaging.comms.provider.
 *
 * uk.co.saiman.messaging.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.messaging.comms.provider is distributed in the hope that it will be useful,
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
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.impl.JSerialPortService.JSerialPortConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * A {@link uk.co.saiman.comms.CommsPort serial port} implementation based on
 * the JSerialComms library.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = JSerialPortConfiguration.class, factory = true)
@Component(configurationPid = JSerialPortService.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class JSerialPortService implements CommsPort {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "JSerialPort Comms Configuration", description = "The JSerialPort component provides native serial port interfaces")
  public @interface JSerialPortConfiguration {
    @AttributeDefinition(name = "Port Name", description = "The name of the port to provide")
    String name();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.comms.jserialport";
  static final String NAME = "name";

  private final Log log;

  private final SerialPort serialPort;

  private final HotObservable<ByteBuffer> receive = new HotObservable<>();

  @Activate
  public JSerialPortService(JSerialPortConfiguration configuration, @Reference Log log) {
    this(SerialPort.getCommPort(configuration.name()), log);
  }

  JSerialPortService(SerialPort serialPort, Log log) {
    this.serialPort = initializePort(serialPort);
    this.log = log;
  }

  private SerialPort initializePort(SerialPort serialPort) {
    try {
      serialPort.setNumDataBits(Byte.SIZE);
      serialPort.setBaudRate(9600);
      serialPort.setNumStopBits(ONE_STOP_BIT);
      serialPort.setParity(NO_PARITY);
      serialPort.setComPortTimeouts(TIMEOUT_READ_BLOCKING | TIMEOUT_WRITE_BLOCKING, 100, 100);
      serialPort.addDataListener(new SerialPortDataListener() {
        @Override
        public void serialEvent(SerialPortEvent event) {
          if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            byte[] bytes = new byte[serialPort.bytesAvailable()];
            serialPort.readBytes(bytes, bytes.length);
            receive.next(ByteBuffer.wrap(bytes).asReadOnlyBuffer());
          }
        }

        @Override
        public int getListeningEvents() {
          return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
        }
      });

      return serialPort;
    } catch (Exception e) {
      Log log = this.log;
      if (log != null)
        log.log(ERROR, e);
      throw e;
    }
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
  public int sendData(ByteBuffer message) throws IOException {
    byte[] bytes = new byte[message.remaining()];
    message.get(bytes);
    return serialPort.writeBytes(bytes, bytes.length);
  }

  @Override
  public Observable<ByteBuffer> receiveData() {
    return receive.map(ByteBuffer::duplicate);
  }
}
