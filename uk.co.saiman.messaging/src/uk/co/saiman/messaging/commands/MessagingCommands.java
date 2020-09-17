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
package uk.co.saiman.messaging.commands;

import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.shell.converters.ShellProperties.COMMAND_FUNCTION_KEY;
import static uk.co.saiman.shell.converters.ShellProperties.COMMAND_SCOPE_PROPERTY;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.felix.service.command.Converter;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.messaging.DataChannel;
import uk.co.saiman.messaging.DataEndpoint;
import uk.co.saiman.messaging.DataReceiver;
import uk.co.saiman.messaging.DataSender;
import uk.co.saiman.messaging.MessageChannel;
import uk.co.saiman.messaging.MessageReceiver;
import uk.co.saiman.messaging.MessageSender;
import uk.co.saiman.shell.converters.RequireConverter;

/**
 * Provide commands to the GoGo shell for interacting with comms.
 * 
 * @author Elias N Vasylenko
 */
@RequireConverter(converterType = ByteBuffer.class)
@Component(immediate = true, property = { COMMAND_SCOPE_PROPERTY, COMMAND_FUNCTION_KEY + "=listEndpoints",
    COMMAND_FUNCTION_KEY + "=getEndpoint", COMMAND_FUNCTION_KEY + "=inspectEndpoint",
    COMMAND_FUNCTION_KEY + "=sendData", COMMAND_FUNCTION_KEY + "=receiveData", COMMAND_FUNCTION_KEY + "=sendMessage",
    COMMAND_FUNCTION_KEY + "=receiveMessage" })
public class MessagingCommands implements Converter {
  private final BundleContext context;
  private final List<ServiceReference<?>> references;
  private final Map<String, DataEndpoint> endpoints;
  private final Map<DataEndpoint, String> ids;

  private void getServices(List<? extends ServiceReference<? extends DataEndpoint>> references) {
    for (var reference : references) {
      var pid = reference.getProperty("service.pid").toString();
      if (!this.endpoints.containsKey(pid)) {
        var service = context.getService(reference);
        this.endpoints.put(pid, service);
        this.ids.putIfAbsent(service, pid);
        this.references.add(reference);
      }
    }
  }

  @Activate
  public MessagingCommands(
      BundleContext context,
      @Reference(policyOption = GREEDY) List<ServiceReference<DataReceiver>> dataReceivers,
      @Reference(policyOption = GREEDY) List<ServiceReference<DataSender>> dataSenders,
      @Reference(policyOption = GREEDY) List<ServiceReference<MessageReceiver>> messageReceivers,
      @Reference(policyOption = GREEDY) List<ServiceReference<MessageSender>> messageSenders) {
    this.context = context;
    this.references = new ArrayList<>();
    this.endpoints = new LinkedHashMap<>();
    this.ids = new HashMap<>();
    getServices(dataReceivers);
    getServices(dataSenders);
    getServices(messageReceivers);
    getServices(messageSenders);
  }

  @Deactivate
  void deactivate() throws Exception {
    references.forEach(context::ungetService);
  }

  @Override
  public Object convert(Class<?> type, Object object) {
    if (!(object instanceof CharSequence)) {
      return null;
    }

    var endpoint = endpoints.get(object.toString());

    if (type.isInstance(endpoint) && (type == DataEndpoint.class || type == DataReceiver.class
        || type == DataSender.class || type == DataChannel.class || type == MessageReceiver.class
        || type == MessageSender.class || type == MessageChannel.class)) {
      return endpoint;
    }

    return null;
  }

  @Override
  public String format(Object object, int p1, Converter p2) {
    return ids.get(object);
  }

  public static final String LIST_DESCRIPTOR = "list all available channels by their system names";

  /**
   * Command: {@value #LIST_DESCRIPTOR}
   *
   * @return a list of all serial channels on the system
   */
  @Descriptor(LIST_DESCRIPTOR)
  public List<String> listEndpoints() {
    return List.copyOf(endpoints.keySet());
  }

  public static final String ENDPOINT_ID = "the PID of an endpoint service";

  public static final String GET_DESCRIPTOR = "fetch a channel by its system name";

  /**
   * Command: {@value #GET_DESCRIPTOR}
   * 
   * @param id {@value #ENDPOINT_ID}
   * @return an endpoint id
   * @throws IOException if the endpoint cannot be found
   */
  @Descriptor(INSPECT_DESCRIPTOR)
  private DataEndpoint getEndpoint(@Descriptor(ENDPOINT_ID) String id) throws IOException {
    return Optional.ofNullable(endpoints.get(id)).orElseThrow(() -> new IOException("Cannot find endpoint"));
  }

  public static final String ENDPOINT = "an endpoint service implementation";

  public static final String INSPECT_DESCRIPTOR = "inspect known details of the given channel";

  /**
   * Command: {@value #INSPECT_DESCRIPTOR}
   * 
   * @param channelId {@value #ENDPOINT_ID}
   * @return a mapping from item names to data
   * @throws IOException if the channel cannot be found
   */
  @Descriptor(INSPECT_DESCRIPTOR)
  public Map<String, String> inspectEndpoint(@Descriptor(ENDPOINT) DataEndpoint endpoint) throws IOException {
    return Map
        .of(
            "id",
            ids.get(endpoint),
            "description",
            endpoint.toString(),
            "receives",
            (endpoint instanceof MessageReceiver ? "messages" : endpoint instanceof DataReceiver ? "data" : "nothing"),
            "sends",
            (endpoint instanceof MessageSender ? "messages" : endpoint instanceof DataSender ? "data" : "nothing"));
  }

  public static final String DATA_BUFFER_SIZE = "the size of the data buffer in bytes";

  public static final String MESSAGE_BUFFER_SIZE = "the size of the message buffer";

  public static final String SEND_DATA_DESCRIPTOR = "write the given bytes to the given channel";
  public static final String SEND_DATA = "the bytes to write to the channel";

  /**
   * Command: {@value #SEND_DATA_DESCRIPTOR}
   * 
   * @param id   {@value #ENDPOINT_ID}
   * @param data {@link #SEND_DATA}
   * @throws IOException problem writing the byte
   */
  @Descriptor(SEND_DATA_DESCRIPTOR)
  public int sendData(@Descriptor(ENDPOINT) DataSender endpoint, @Descriptor(SEND_DATA) ByteBuffer data)
      throws IOException {
    return endpoint.sendData(data);
  }

  public static final String RECEIVE_DATA_BYTES_DESCRIPTOR = "read a number of bytes from the open channel";
  public static final String RECEIVE_BYTE_COUNT = "the number of bytes to read";
  public static final String RECEIVE_TIMEOUT = "timeout";
  public static final String RECEIVE_TIME_UNIT = "time unit";

  /**
   * Command: {@value #RECEIVE_DATA_BYTES_DESCRIPTOR}
   * 
   * @param id        {@value #ENDPOINT_ID}
   * @param byteCount {@value #RECEIVE_BYTE_COUNT}
   * @return the bytes read from the channel
   * @throws IOException problem reading the bytes
   */
  @Descriptor(RECEIVE_DATA_BYTES_DESCRIPTOR)
  public ByteBuffer receiveData(
      @Descriptor(ENDPOINT) DataReceiver endpoint,
      @Descriptor(RECEIVE_BYTE_COUNT) int byteCount,
      @Descriptor(RECEIVE_TIMEOUT) long timeout,
      @Descriptor(RECEIVE_TIME_UNIT) TimeUnit unit)
      throws IOException {
    return endpoint.packeting(byteCount).receiveData().getNext().orTimeout(timeout, unit).join();
  }

  public static final String SEND_MESSAGE_DESCRIPTOR = "write the given message to the given channel";
  public static final String SEND_MESSAGE = "the message to write to the channel";

  /**
   * Command: {@value #SEND_MESSAGE_DESCRIPTOR}
   * 
   * @param id   {@value #ENDPOINT_ID}
   * @param data {@link #SEND_MESSAGE}
   * @throws IOException problem writing the byte
   */
  @Descriptor(SEND_MESSAGE_DESCRIPTOR)
  public void sendMessage(@Descriptor(ENDPOINT) MessageSender endpoint, @Descriptor(SEND_MESSAGE) ByteBuffer data)
      throws IOException {
    endpoint.sendMessage(data);
  }

  public static final String RECEIVE_MESSAGE_DESCRIPTOR = "read a message the open channel";

  /**
   * Command: {@value #RECEIVE_MESSAGE_DESCRIPTOR}
   * 
   * @param id {@value #ENDPOINT_ID}
   * @return the message read from the channel
   * @throws IOException problem reading the message
   */
  @Descriptor(RECEIVE_MESSAGE_DESCRIPTOR)
  public ByteBuffer receiveMessage(
      @Descriptor(ENDPOINT) MessageReceiver endpoint,
      @Descriptor(RECEIVE_TIMEOUT) long timeout,
      @Descriptor(RECEIVE_TIME_UNIT) TimeUnit unit)
      throws IOException {
    return endpoint.receiveMessages().getNext().orTimeout(timeout, unit).join();
  }
}
