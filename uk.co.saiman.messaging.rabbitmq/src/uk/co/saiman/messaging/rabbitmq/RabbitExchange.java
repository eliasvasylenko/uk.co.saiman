
package uk.co.saiman.messaging.rabbitmq;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.messaging.rabbitmq.RabbitExchange.RabbitExchangeConfiguration;

@Designate(ocd = RabbitExchangeConfiguration.class, factory = true)
@Component(configurationPid = RabbitExchange.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = RabbitExchange.class)
public class RabbitExchange {
  static final String CONFIGURATION_PID = "uk.co.saiman.messaging.rabbitmq.exchange";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Rabbit MQ Exchange", description = "A servive for interfacing with a Rabbit MQ message exchange")
  public @interface RabbitExchangeConfiguration {
    @AttributeDefinition(name = "Exchange Name", description = "The name of the exchange")
    String exchangeName();

    @AttributeDefinition(name = "Exchange Type", description = "The type of the exchange")
    String exchangeType() default "fanout";
  }

  private final RabbitHost host;
  private final String exchangeName;
  private final String exchangeType;
  private final Log log;

  private Connection connection;
  private Channel channel;

  @Activate
  public RabbitExchange(
      RabbitExchangeConfiguration configuration,
      @Reference(name = "host") RabbitHost host,
      @Reference Log log)
      throws KeyManagementException,
      NoSuchAlgorithmException,
      URISyntaxException {
    this(host, configuration.exchangeName(), configuration.exchangeType(), log);
  }

  public RabbitExchange(RabbitHost host, String exchangeName, String exchangeType, Log log) {
    this.host = host;
    this.exchangeName = exchangeName;
    this.exchangeType = exchangeType;
    this.log = log;
  }

  @Deactivate
  public void deactivate() throws IOException {
    closeChannel();
  }

  public String getName() {
    return exchangeName;
  }

  public String getType() {
    return exchangeType;
  }

  protected synchronized Channel openChannel() throws IOException {
    if (channel == null) {
      try {
        connection = host.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, exchangeType);
      } catch (TimeoutException e) {
        channel = null;
        log.log(Level.ERROR, e);
        throw new IOException(e);
      } catch (IOException e) {
        channel = null;
        log.log(Level.ERROR, e);
        throw e;
      }
    }

    return channel;
  }

  protected synchronized void closeChannel() throws IOException {
    if (channel != null) {
      try {
        channel.close();
        connection.close();
        channel = null;
        connection = null;
      } catch (TimeoutException e) {
        log.log(Level.ERROR, e);
        throw new IOException(e);
      } catch (IOException e) {
        log.log(Level.ERROR, e);
        throw e;
      }
    }
  }
}
