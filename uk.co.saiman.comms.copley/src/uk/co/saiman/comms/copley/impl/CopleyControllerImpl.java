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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.bytes.ByteConverters;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.InvalidCommsPort;
import uk.co.saiman.comms.SimpleCommsController;
import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyNode;
import uk.co.saiman.comms.copley.impl.CopleyControllerImpl.CopleyCommsConfiguration;
import uk.co.saiman.function.ThrowingFunction;

@Designate(ocd = CopleyCommsConfiguration.class, factory = true)
@Component(
    name = CopleyControllerImpl.CONFIGURATION_PID,
    configurationPid = CopleyControllerImpl.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CopleyControllerImpl extends SimpleCommsController implements CopleyController {
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

  @Reference
  private ByteConverters converters;

  private final Map<CopleyNodeImpl, ServiceRegistration<CopleyNode>> nodes = new LinkedHashMap<>();
  private final Map<CopleyAxis, ServiceRegistration<CopleyAxis>> axes = new LinkedHashMap<>();

  private BundleContext context;

  @Activate
  void activate(CopleyCommsConfiguration configuration, BundleContext context) throws IOException {
    this.context = context;
    if (port == null) {
      port = new InvalidCommsPort(configuration.port_target());
    }
    super.activate(port);
  }

  @Deactivate
  @Override
  protected void deactivate() {
    super.deactivate();
  }

  @Override
  public void reset() {
    super.reset();
  }

  @Override
  protected synchronized <U> U useChannel(ThrowingFunction<ByteChannel, U, Exception> action) {
    return super.useChannel(action);
  }

  @Override
  protected synchronized CommsException setFault(CommsException commsException) {
    return super.setFault(commsException);
  }

  @Override
  protected void commsOpened() {
    List<CopleyNodeImpl> nodes = listNodes();

    nodes.forEach(node -> {
      this.nodes.put(node, context.registerService(CopleyNode.class, node, getProperties(node)));

      node.getAxes().forEach(axis -> {
        this.axes.put(axis, context.registerService(CopleyAxis.class, axis, getProperties(axis)));
      });
    });
  }

  private Dictionary<String, String> getProperties(CopleyNode node) {
    Dictionary<String, String> properties = new Hashtable<>();
    return properties;
  }

  private Dictionary<String, String> getProperties(CopleyAxis axis) {
    Dictionary<String, String> properties = new Hashtable<>();
    return properties;
  }

  @Override
  protected void commsClosed() {
    nodes.values().forEach(n -> n.unregister());
    nodes.clear();
    axes.values().forEach(n -> n.unregister());
    axes.clear();
  }

  @Override
  protected void checkComms() {
    nodes.keySet().forEach(n -> n.ping());
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

  ByteConverters getConverters() {
    return converters;
  }

  @Override
  public Stream<CopleyNode> getNodes() {
    return upcastStream(nodes.keySet().stream());
  }
}
