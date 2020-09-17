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
    return factory.clone().newConnection();
  }
}
