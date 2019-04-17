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
 * This file is part of uk.co.saiman.messaging.rabbitmq.
 *
 * uk.co.saiman.messaging.rabbitmq is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.messaging.rabbitmq is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.saiman.messaging.rabbitmq;

import static java.nio.ByteBuffer.wrap;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.messaging.DataReceiver;
import uk.co.saiman.messaging.MessageReceiver;
import uk.co.saiman.messaging.rabbitmq.RabbitQueue.RabbitQueueConfiguration;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

@Designate(ocd = RabbitQueueConfiguration.class, factory = true)
@Component(configurationPid = RabbitQueue.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class RabbitQueue implements MessageReceiver, DataReceiver {
  static final String CONFIGURATION_PID = "uk.co.saiman.messaging.rabbitmq.queue";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Rabbit MQ Queue", description = "A servive for interfacing with a Rabbit MQ message exchange")
  public @interface RabbitQueueConfiguration {
    @AttributeDefinition(name = "Routing Key", description = "The routing key for the channel")
    String routingKey() default "";
  }

  private final RabbitExchange exchange;
  private final String routingKey;
  private final Log log;

  private String queue;
  private HotObservable<ByteBuffer> receive;

  @Activate
  public RabbitQueue(
      RabbitQueueConfiguration configuration,
      @Reference(name = "exchange") RabbitExchange exchange,
      @Reference Log log)
      throws KeyManagementException,
      NoSuchAlgorithmException,
      URISyntaxException {
    this(exchange, configuration.routingKey(), log);
  }

  public RabbitQueue(RabbitExchange exchange, String routingKey, Log log) {
    this.exchange = exchange;
    this.routingKey = routingKey;
    this.log = log;

    this.receive = new HotObservable<>() {
      protected void open() throws IOException {
        openQueue();
      }
    };
  }

  @Deactivate
  public void deactivate() throws IOException {
    closeQueue();
  }

  protected synchronized String openQueue() throws IOException {
    var channel = exchange.openChannel();

    try {
      var queue = channel.queueDeclare().getQueue();
      channel.queueBind(queue, exchange.getName(), routingKey);

      this.queue = queue;

      /*
       * TODO should the service come and go based on availability of the queue????
       * 
       * TODO should an error state be recoverable? How should recovery be triggered
       * by consumer?
       * 
       * TODO what happens if we successfully create the queue but FAIL to create the
       * consumer?
       */

      var consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(
            String consumerTag,
            Envelope envelope,
            AMQP.BasicProperties properties,
            byte[] body)
            throws IOException {
          receive.next(wrap(body));
        }
      };
      channel.basicConsume(queue, true, consumer);

      return queue;
    } catch (IOException e) {
      channel = null;
      log.log(Level.ERROR, e);
      throw e;
    }
  }

  protected synchronized void closeQueue() throws IOException {
    if (queue != null) {
      try {
        exchange.openChannel().queueDelete(queue);
        queue = null;
      } catch (IOException e) {
        log.log(Level.ERROR, e);
        throw e;
      }
    }
  }

  @Override
  public Observable<ByteBuffer> receiveMessages() {
    return receive;
  }
}