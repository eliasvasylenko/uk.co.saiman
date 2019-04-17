
package uk.co.saiman.messaging.rabbitmq;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import uk.co.saiman.messaging.rabbitmq.RabbitHost.RabbitHostConfiguration;

@Designate(ocd = RabbitHostConfiguration.class, factory = true)
@Component(configurationPid = RabbitHost.CONFIGURATION_PID, configurationPolicy = REQUIRE, service = RabbitHost.class)
public class RabbitHost {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Rabbit MQ Host", description = "A servive for interfacing with a Rabbit MQ message broker")
  public @interface RabbitHostConfiguration {
    @AttributeDefinition(name = "URL", description = "The URL to use when connecting with the broker")
    String url();

    @AttributeDefinition(name = "Password", description = "The password to use when connecting with the broker")
    String password();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.messaging.rabbitmq.host";

  private final URI uri;
  private final ConnectionFactory factory;

  @Activate
  public RabbitHost(RabbitHostConfiguration configuration)
      throws KeyManagementException,
      NoSuchAlgorithmException,
      URISyntaxException {
    this(new URI(configuration.url()), configuration.password());
  }

  public RabbitHost(URI uri, String password)
      throws KeyManagementException,
      NoSuchAlgorithmException,
      URISyntaxException {
    this.uri = uri;
    this.factory = new ConnectionFactory();
    this.factory.setUri(uri);
    this.factory.setPassword(password);
    // factory.setCredentialsProvider(creds);
  }

  @Override
  public String toString() {
    return getClass().getName() + "(" + uri + ")";
  }

  public URI getUri() {
    return uri;
  }

  public Connection newConnection() throws IOException, TimeoutException {
    return factory.newConnection();
  }
}
