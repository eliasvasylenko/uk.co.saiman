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
package uk.co.saiman.messaging;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.nio.ByteBuffer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.messaging.MessagePipe.MessagePipeConfiguration;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

@Designate(ocd = MessagePipeConfiguration.class, factory = true)
@Component(configurationPid = MessagePipe.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = {
    MessageChannel.class,
    MessageSender.class,
    MessageReceiver.class,
    DataChannel.class,
    DataSender.class,
    DataReceiver.class })
public class MessagePipe implements MessageChannel {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Message Pipe Configuration", description = "A simple message channel that pipes written messages back to itself to be read")
  public @interface MessagePipeConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.messaging.pipe";

  private final HotObservable<ByteBuffer> messages;

  public MessagePipe() {
    this.messages = new HotObservable<>();
  }

  @Override
  public void sendMessage(ByteBuffer message) {
    messages.next(message);
  }

  @Override
  public Observable<ByteBuffer> receiveMessages() {
    return messages;
  }
}
