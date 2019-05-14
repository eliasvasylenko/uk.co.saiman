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
package uk.co.saiman.messaging.commands;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.messaging.commands.MessagingCommands.COMMAND_FUNCTION_KEY;
import static uk.co.saiman.messaging.commands.MessagingCommands.COMMAND_SCOPE_KEY;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import uk.co.saiman.messaging.DataBuffer;
import uk.co.saiman.messaging.DataReceiver;
import uk.co.saiman.messaging.DataSender;
import uk.co.saiman.messaging.MessageBuffer;
import uk.co.saiman.messaging.MessageReceiver;
import uk.co.saiman.messaging.MessageSender;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;
import uk.co.saiman.shell.converters.RequireConverter;

/**
 * Provide commands to the GoGo shell for interacting with comms.
 * 
 * @author Elias N Vasylenko
 */
@RequireConverter(converterType = ByteBuffer.class)
@Component(immediate = true, service = MessagingCommands.class, property = {
    COMMAND_SCOPE_KEY + "=channel",
    COMMAND_FUNCTION_KEY + "=openDataBuffer",
    COMMAND_FUNCTION_KEY + "=openMessageBuffer",
    COMMAND_FUNCTION_KEY + "=closeBuffers",
    COMMAND_FUNCTION_KEY + "=sendData",
    COMMAND_FUNCTION_KEY + "=receiveData",
    COMMAND_FUNCTION_KEY + "=sendMessage",
    COMMAND_FUNCTION_KEY + "=receiveMessage",
    COMMAND_FUNCTION_KEY + "=list",
    COMMAND_FUNCTION_KEY + "=inspect" })
public class MessagingCommands {
  public static final String COMMAND_SCOPE_KEY = "osgi.command.scope";
  public static final String COMMAND_FUNCTION_KEY = "osgi.command.function";

  private final ServiceIndex<?, String, DataReceiver> dataReceiverIndex;
  private final ServiceIndex<?, String, DataSender> dataSenderIndex;
  private final ServiceIndex<?, String, MessageReceiver> messageReceiverIndex;
  private final ServiceIndex<?, String, MessageSender> messageSenderIndex;

  private Map<ServiceRecord<?, String, ?>, DataBuffer> dataBuffers = new HashMap<>();
  private Map<ServiceRecord<?, String, ?>, MessageBuffer> messageBuffers = new HashMap<>();

  @Activate
  public MessagingCommands(BundleContext context) {
    dataReceiverIndex = ServiceIndex.open(context, DataReceiver.class);
    dataSenderIndex = ServiceIndex.open(context, DataSender.class);
    messageReceiverIndex = ServiceIndex.open(context, MessageReceiver.class);
    messageSenderIndex = ServiceIndex.open(context, MessageSender.class);
  }

  @Deactivate
  void deactivate() throws Exception {
    dataReceiverIndex.close();
    dataSenderIndex.close();
    messageReceiverIndex.close();
    messageSenderIndex.close();
    for (var dataBuffer : dataBuffers.values()) {
      dataBuffer.close();
    }
    for (var messageBuffer : messageBuffers.values()) {
      messageBuffer.close();
    }
  }

  private ServiceRecord<?, String, ?> get(String id) throws IOException {
    return Stream
        .of(dataReceiverIndex, messageReceiverIndex, dataSenderIndex, messageSenderIndex)
        .flatMap(index -> index.get(id).stream())
        .findAny()
        .orElseThrow(() -> new IOException("Cannot find channel"));
  }

  private Stream<String> records() {
    return Stream
        .of(dataReceiverIndex, messageReceiverIndex, dataSenderIndex, messageSenderIndex)
        .flatMap(ServiceIndex::records)
        .map(ServiceRecord::id)
        .flatMap(Optional::stream)
        .distinct();
  }

  public static final String CHANNEL_ID = "the PID of the channel service";

  public static final String DATA_BUFFER_SIZE = "the size of the data buffer in bytes";

  public static final String OPEN_DATA_DESCRIPTOR = "open the given channel for reading data";

  /**
   * Command: {@value #OPEN_DATA_DESCRIPTOR}
   * 
   * @param id {@value #CHANNEL_ID}
   * @throws IOException problem opening the channel
   */
  @Descriptor(OPEN_DATA_DESCRIPTOR)
  public void openDataBuffer(
      @Descriptor(CHANNEL_ID) String id,
      @Descriptor(DATA_BUFFER_SIZE) int bufferSize)
      throws IOException {
    var channel = get(id);

    if (!(channel.serviceObject() instanceof DataReceiver)) {
      throw new IOException(format("Cannot open channel %s for receiving data", channel));
    }

    synchronized (dataBuffers) {
      dataBuffers.get(channel).close();
      dataBuffers.put(channel, ((DataReceiver) channel.serviceObject()).openDataBuffer(bufferSize));
    }
  }

  public static final String MESSAGE_BUFFER_SIZE = "the size of the message buffer";

  public static final String OPEN_MESSAGE_DESCRIPTOR = "open the given channel for reading messages";

  /**
   * Command: {@value #OPEN_MESSAGE_DESCRIPTOR}
   * 
   * @param id {@value #CHANNEL_ID}
   * @throws IOException problem opening the channel
   */
  @Descriptor(OPEN_MESSAGE_DESCRIPTOR)
  public void openMessageBuffer(
      @Descriptor(CHANNEL_ID) String id,
      @Descriptor(MESSAGE_BUFFER_SIZE) int bufferSize)
      throws IOException {
    var channel = get(id);

    if (!(channel.serviceObject() instanceof MessageReceiver)) {
      throw new IOException(format("Cannot open channel %s for receiving messages", channel));
    }

    synchronized (messageBuffers) {
      messageBuffers.get(channel).close();
      messageBuffers
          .put(channel, ((MessageReceiver) channel.serviceObject()).openMessageBuffer(bufferSize));
    }
  }

  public static final String CLOSE_BUFFERS_DESCRIPTOR = "close any open buffers for the given channel";

  /**
   * Command: {@value #CLOSE_BUFFERS_DESCRIPTOR}
   * 
   * @param id {@value #CHANNEL_ID}
   * @throws IOException problem closing the channel
   */
  @Descriptor(CLOSE_BUFFERS_DESCRIPTOR)
  public void closeBuffers(@Descriptor(CHANNEL_ID) String id) throws IOException {
    var channel = get(id);

    synchronized (dataBuffers) {
      var dataBuffer = dataBuffers.get(channel);
      if (dataBuffer != null) {
        dataBuffer.close();
      }
    }
    synchronized (messageBuffers) {
      var messageBuffer = messageBuffers.get(channel);
      if (messageBuffer != null) {
        messageBuffer.close();
      }
    }
  }

  public static final String SEND_DATA_DESCRIPTOR = "write the given bytes to the given channel";
  public static final String SEND_DATA = "the bytes to write to the channel";

  /**
   * Command: {@value #SEND_DATA_DESCRIPTOR}
   * 
   * @param id   {@value #CHANNEL_ID}
   * @param data {@link #SEND_DATA}
   * @throws IOException problem writing the byte
   */
  @Descriptor(SEND_DATA_DESCRIPTOR)
  public int sendData(@Descriptor(CHANNEL_ID) String id, @Descriptor(SEND_DATA) ByteBuffer data)
      throws IOException {
    var channel = get(id);

    if (!(channel.serviceObject() instanceof DataSender)) {
      throw new IOException(format("Cannot open channel %s for sending data", channel));
    }

    return ((DataSender) channel.serviceObject()).sendData(data);
  }

  public static final String RECEIVE_DATA_BYTES_DESCRIPTOR = "read a number of bytes from the open channel";
  public static final String RECEIVE_BYTE_COUNT = "the number of bytes to read";

  /**
   * Command: {@value #RECEIVE_DATA_BYTES_DESCRIPTOR}
   * 
   * @param id        {@value #CHANNEL_ID}
   * @param byteCount {@value #RECEIVE_BYTE_COUNT}
   * @return the bytes read from the channel
   * @throws IOException problem reading the bytes
   */
  @Descriptor(RECEIVE_DATA_BYTES_DESCRIPTOR)
  public ByteBuffer receiveData(
      @Descriptor(CHANNEL_ID) String id,
      @Descriptor(RECEIVE_BYTE_COUNT) int byteCount)
      throws IOException {
    var channel = get(id);

    DataBuffer dataBuffer;
    synchronized (dataBuffers) {
      dataBuffer = dataBuffers.get(channel);
    }

    if (dataBuffer == null) {
      throw new IOException(
          format("No data buffer is open for channel %s", channel.serviceObject()));
    }

    ByteBuffer buffer = ByteBuffer.allocate(byteCount);
    dataBuffer.readData(buffer);
    buffer.flip();

    return buffer;
  }

  public static final String RECEIVE_DATA_DESCRIPTOR = "read all available bytes from the open channel";

  /**
   * Command: {@value #RECEIVE_DATA_DESCRIPTOR}
   * 
   * @param id {@value #CHANNEL_ID}
   * @return the bytes read from the channel
   * @throws IOException problem reading the bytes
   */
  @Descriptor(RECEIVE_DATA_DESCRIPTOR)
  public ByteBuffer receiveData(@Descriptor(CHANNEL_ID) String id) throws IOException {
    var channel = get(id);

    DataBuffer dataBuffer;
    synchronized (dataBuffers) {
      dataBuffer = dataBuffers.get(channel);
    }

    if (dataBuffer == null) {
      throw new IOException(
          format("No data buffer is open for channel %s", channel.serviceObject()));
    }

    ByteBuffer buffer = ByteBuffer.allocate(dataBuffer.availableBytes());
    dataBuffer.readData(buffer);
    buffer.flip();

    return buffer;
  }

  public static final String SEND_MESSAGE_DESCRIPTOR = "write the given message to the given channel";
  public static final String SEND_MESSAGE = "the message to write to the channel";

  /**
   * Command: {@value #SEND_MESSAGE_DESCRIPTOR}
   * 
   * @param id   {@value #CHANNEL_ID}
   * @param data {@link #SEND_MESSAGE}
   * @throws IOException problem writing the byte
   */
  @Descriptor(SEND_MESSAGE_DESCRIPTOR)
  public void sendMessage(
      @Descriptor(CHANNEL_ID) String id,
      @Descriptor(SEND_MESSAGE) ByteBuffer data)
      throws IOException {
    var channel = get(id);

    if (!(channel.serviceObject() instanceof MessageSender)) {
      throw new IOException(format("Cannot open channel %s for sending messages", channel));
    }

    ((MessageSender) channel.serviceObject()).sendMessage(data);
  }

  public static final String RECEIVE_MESSAGE_DESCRIPTOR = "read a message the open channel";

  /**
   * Command: {@value #RECEIVE_MESSAGE_DESCRIPTOR}
   * 
   * @param id {@value #CHANNEL_ID}
   * @return the message read from the channel
   * @throws IOException problem reading the message
   */
  @Descriptor(RECEIVE_MESSAGE_DESCRIPTOR)
  public ByteBuffer receiveMessage(@Descriptor(CHANNEL_ID) String id) throws IOException {
    var channel = get(id);

    MessageBuffer messageBuffer;
    synchronized (messageBuffers) {
      messageBuffer = messageBuffers.get(channel);
    }

    if (messageBuffer == null) {
      throw new IOException(
          format("No data buffer is open for channel %s", channel.serviceObject()));
    }

    return messageBuffer.readMessage();
  }

  public static final String LIST_DESCRIPTOR = "list all available channels by their system names";

  /**
   * Command: {@value #LIST_DESCRIPTOR}
   *
   * @return a list of all serial channels on the system
   */
  @Descriptor(LIST_DESCRIPTOR)
  public List<String> list() {
    return records().collect(toList());
  }

  public static final String INSPECT_DESCRIPTOR = "inspect known details of the given channel";

  /**
   * Command: {@value #INSPECT_DESCRIPTOR}
   * 
   * @param channelId {@value #CHANNEL_ID}
   * @return a mapping from item names to data
   * @throws IOException if the channel cannot be found
   */
  @Descriptor(INSPECT_DESCRIPTOR)
  public Map<String, String> inspect(@Descriptor(CHANNEL_ID) String channelId) throws IOException {
    return inspect(get(channelId));
  }

  private Map<String, String> inspect(ServiceRecord<?, String, ?> channel) {
    var service = channel.serviceObject();
    return Map
        .of(
            "dataBufferOpen",
            (dataBuffers.containsKey(channel) ? "yes" : "no"),
            "messageBufferOpen",
            (messageBuffers.containsKey(channel) ? "yes" : "no"),
            "id",
            channel.id().orElse(null),
            "description",
            service.toString(),
            "receives",
            (service instanceof MessageReceiver
                ? " messages"
                : service instanceof DataReceiver ? " data" : "nothing"),
            "sends",
            (service instanceof MessageSender
                ? " messages"
                : service instanceof DataSender ? " data" : "nothing"));
  }
}
