
package uk.co.saiman.messaging.rabbitmq;

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

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.messaging.DataSender;
import uk.co.saiman.messaging.MessageSender;
import uk.co.saiman.messaging.rabbitmq.RabbitPublisher.RabbitPublisherConfiguration;

@Designate(ocd = RabbitPublisherConfiguration.class, factory = true)
@Component(configurationPid = RabbitPublisher.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class RabbitPublisher implements MessageSender, DataSender {
  static final String CONFIGURATION_PID = "uk.co.saiman.messaging.rabbitmq.publisher";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Rabbit MQ Publisher", description = "A servive for interfacing with a Rabbit MQ message exchange")
  public @interface RabbitPublisherConfiguration {
    @AttributeDefinition(name = "Routing Key", description = "The routing key for the channel")
    String routingKey() default "";
  }

  private final RabbitExchange exchange;
  private final String routingKey;
  private final Log log;

  @Activate
  public RabbitPublisher(
      RabbitPublisherConfiguration configuration,
      @Reference(name = "exchange") RabbitExchange exchange,
      @Reference Log log)
      throws KeyManagementException,
      NoSuchAlgorithmException,
      URISyntaxException {
    this(exchange, configuration.routingKey(), log);
  }

  public RabbitPublisher(RabbitExchange exchange, String routingKey, Log log) {
    this.exchange = exchange;
    this.routingKey = routingKey;
    this.log = log;
  }

  @Deactivate
  public void deactivate() {}

  @Override
  public void sendMessage(ByteBuffer message) throws IOException {
    try {
      exchange.openChannel().basicPublish(exchange.getName(), routingKey, null, message.array());
    } catch (IOException e) {
      log.log(Level.ERROR, e);
      throw e;
    }
  }
}