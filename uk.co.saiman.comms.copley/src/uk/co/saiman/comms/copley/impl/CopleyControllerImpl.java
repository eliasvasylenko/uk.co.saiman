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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import static java.util.Collections.singletonList;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.STATIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.collection.StreamUtilities.upcastStream;
import static uk.co.saiman.comms.copley.ErrorCode.INVALID_NODE_ID;
import static uk.co.saiman.comms.copley.impl.CopleyNodeImpl.NODE_ID_MASK;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.bytes.ByteConverters;
import uk.co.saiman.comms.CommsChannel;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.InvalidCommsPort;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyNode;
import uk.co.saiman.comms.copley.impl.CopleyControllerImpl.CopleyCommsConfiguration;
import uk.co.saiman.function.ThrowingFunction;

@Designate(ocd = CopleyCommsConfiguration.class, factory = true)
@Component(
    name = CopleyControllerImpl.CONFIGURATION_PID,
    configurationPid = CopleyControllerImpl.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyControllerImpl implements CopleyController {
  static final String CONFIGURATION_PID = "uk.co.saiman.comms.copley";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "Copley Comms Configuration",
      description = "The configuration for the underlying serial comms for a Copley motor control")
  public @interface CopleyCommsConfiguration {
    @AttributeDefinition(name = "Serial Port", description = "The serial port for comms")
    String port_target();
  }

  @Reference(cardinality = OPTIONAL, policy = STATIC, policyOption = GREEDY)
  private CommsPort port;
  private CommsChannel channel;
  private CommsException fault;

  @Reference
  ByteConverters converters;

  List<CopleyNodeImpl> nodes = new ArrayList<>();

  @Activate
  void activate(CopleyCommsConfiguration configuration) throws IOException {
    if (port == null) {
      port = new InvalidCommsPort(configuration.port_target());
    }
    reset();
  }

  @Deactivate
  void deactivate() throws IOException {
    if (channel != null) {
      channel.close();
      channel = null;
    }
  }

  @Override
  public synchronized void reset() {
    try {
      if (fault != null) {
        fault = null;
      }
      if (channel == null || !channel.isOpen()) {
        nodes.clear();

        channel = port.openChannel();
        channel.read();

        nodes.addAll(listNodes());
      }

      ping();
    } catch (CommsException e) {
      throw setFault(e);
    } catch (Exception e) {
      throw setFault(new CommsException("Problem opening comms", e));
    }
  }

  private List<CopleyNodeImpl> listNodes() {
    try {
      return singletonList(new CopleyNodeImpl(this));
    } catch (CopleyErrorException e) {
      if (e.getCode() != INVALID_NODE_ID) {
        throw e;
      }
    }

    List<CopleyNodeImpl> nodes = new ArrayList<>();
    do {
      try {
        nodes.add(new CopleyNodeImpl(this, nodes.size()));
      } catch (CopleyErrorException e) {
        if (e.getCode() == INVALID_NODE_ID) {
          break;
        } else {
          throw e;
        }
      }
    } while (nodes.size() < NODE_ID_MASK);
    return nodes;
  }

  protected synchronized CommsException setFault(CommsException commsException) {
    this.fault = commsException;
    return commsException;
  }

  @Override
  public CommsPort getPort() {
    return port;
  }

  ByteConverters getConverters() {
    return converters;
  }

  @Override
  public Stream<CopleyNode> getNodes() {
    return upcastStream(nodes.stream());
  }

  protected void ping() {
    nodes.forEach(n -> n.ping());
  }

  protected synchronized <U> U useChannel(ThrowingFunction<ByteChannel, U, Exception> action) {
    try {
      if (fault != null)
        throw fault;

      if (channel == null || !channel.isOpen())
        throw new CommsException("Port is closed");

      return action.apply(channel);
    } catch (CommsException e) {
      throw e;
    } catch (Exception e) {
      throw new CommsException("Problem transferring data", e);
    }
  }
}
