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

import static java.util.stream.Collectors.toList;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static uk.co.saiman.text.properties.SaiProperties.SAI_COMMAND_SCOPE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.felix.service.command.Descriptor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.shell.converters.RequireConverter;

/**
 * Provide commands to the GoGo shell for interacting with comms.
 * 
 * @author Elias N Vasylenko
 */
@RequireConverter(converterType = ByteBuffer.class)
@Component(
    immediate = true,
    service = CommsCommands.class, // not sure why this seems to be needed ...
    property = {
        "osgi.command.scope" + "=" + SAI_COMMAND_SCOPE,
        "osgi.command.function" + "=openPort",
        "osgi.command.function" + "=closePort",
        "osgi.command.function" + "=readPort",
        "osgi.command.function" + "=writePort",
        "osgi.command.function" + "=listPorts",
        "osgi.command.function" + "=inspectPort" })
public class CommsCommands {
  @Reference(policy = DYNAMIC)
  private volatile List<CommsPort> ports = new CopyOnWriteArrayList<>();
  private CommsPort openPort;
  private CommsChannel openChannel;

  void deactivate() throws IOException {
    if (openPort != null) {
      closePort();
    }
  }

  private void assertPortAvailable() {
    if (openPort == null)
      throw new CommsException("No port is open here");
  }

  public static final String PORT_NAME = "the system name of the serial port";

  public static final String OPEN_PORT_DESCRIPTOR = "open the given port";

  private CommsPort getPort(String portName) {
    return ports
        .stream()
        .filter(p -> p.getName().equals(portName))
        .findAny()
        .orElseThrow(() -> new CommsException("Cannot find port"));
  }

  /**
   * Command: {@value #OPEN_PORT_DESCRIPTOR}
   * 
   * @param portName
   *          {@value #PORT_NAME}
   * @throws IOException
   *           problem opening the channel
   */
  @Descriptor(OPEN_PORT_DESCRIPTOR)
  public void openPort(@Descriptor(PORT_NAME) String portName) throws IOException {
    if (openPort != null) {
      throw new CommsException("Port already open " + openPort);
    }

    CommsPort port = getPort(portName);
    openChannel = port.openChannel();
    openPort = port;
  }

  public static final String CLOSE_PORT_DESCRIPTOR = "close the given port";

  /**
   * Command: {@value #CLOSE_PORT_DESCRIPTOR}
   * 
   * @throws IOException
   *           problem closing the channel
   */
  @Descriptor(CLOSE_PORT_DESCRIPTOR)
  public void closePort() throws IOException {
    assertPortAvailable();
    openPort = null;
    CommsChannel channel = openChannel;
    openChannel = null;
    channel.close();
  }

  public static final String READ_PORT_BYTES_DESCRIPTOR = "read a number of bytes from the given port";
  public static final String READ_PORT_BYTE_COUNT = "the number of bytes to read";

  /**
   * Command: {@value #READ_PORT_BYTES_DESCRIPTOR}
   * 
   * @param byteCount
   *          {@value #READ_PORT_BYTE_COUNT}
   * @return the bytes read from the port
   * @throws IOException
   *           problem reading the bytes
   */
  @Descriptor(READ_PORT_BYTES_DESCRIPTOR)
  public ByteBuffer readPort(@Descriptor(READ_PORT_BYTE_COUNT) int byteCount) throws IOException {
    assertPortAvailable();

    ByteBuffer buffer = ByteBuffer.allocate(byteCount);
    openChannel.read(buffer);
    buffer.flip();

    return buffer;
  }

  public static final String READ_PORT_DESCRIPTOR = "read all available bytes from the given port";

  /**
   * Command: {@value #READ_PORT_DESCRIPTOR}
   * 
   * @return the bytes read from the port
   * @throws IOException
   *           problem reading the bytes
   */
  @Descriptor(READ_PORT_DESCRIPTOR)
  public ByteBuffer readPort() throws IOException {
    assertPortAvailable();

    ByteBuffer buffer = ByteBuffer.allocate(openChannel.bytesAvailable());
    openChannel.read(buffer);
    buffer.flip();

    return buffer;
  }

  public static final String WRITE_PORT_DESCRIPTOR = "write the given byte to the given port";
  public static final String WRITE_PORT_DATA = "the byte to write to the port";

  /**
   * Command: {@value #WRITE_PORT_DESCRIPTOR}
   * 
   * @param data
   *          {@link #WRITE_PORT_DATA}
   * @throws IOException
   *           problem writing the byte
   */
  @Descriptor(WRITE_PORT_DESCRIPTOR)
  public void writePort(@Descriptor(WRITE_PORT_DATA) ByteBuffer data) throws IOException {
    assertPortAvailable();

    openChannel.write(data);
  }

  public static final String LIST_PORTS_DESCRIPTOR = "list all available ports by their system names";

  /**
   * Command: {@value #LIST_PORTS_DESCRIPTOR}
   *
   * @return a list of all serial ports on the system
   */
  @Descriptor(LIST_PORTS_DESCRIPTOR)
  public List<String> listPorts() {
    return ports
        .stream()
        .map(CommsPort::getName)
        .map(n -> n + (openPort != null && openPort.getName().equals(n) ? "*" : ""))
        .collect(toList());
  }

  public static final String INSPECT_PORT_DESCRIPTOR = "inspect known details of the given port";

  /**
   * Command: {@value #INSPECT_PORT_DESCRIPTOR}
   * 
   * @param portName
   *          {@value #PORT_NAME}
   * @return a mapping from item names to data
   */
  @Descriptor(INSPECT_PORT_DESCRIPTOR)
  public Map<String, String> inspectPort(@Descriptor(PORT_NAME) String portName) {
    CommsPort port = getPort(portName);

    Map<String, String> properties = new LinkedHashMap<>();

    properties.put("Name", port.getName());
    properties.put("Description", port.toString());

    return properties;
  }
}
