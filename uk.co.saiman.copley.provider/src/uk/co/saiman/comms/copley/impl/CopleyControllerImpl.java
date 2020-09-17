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
 * This file is part of uk.co.saiman.copley.provider.
 *
 * uk.co.saiman.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley.provider is distributed in the hope that it will be useful,
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
import static uk.co.saiman.collection.StreamUtilities.upcastStream;
import static uk.co.saiman.comms.copley.ErrorCode.INVALID_NODE_ID;
import static uk.co.saiman.comms.copley.impl.CopleyNodeImpl.NODE_ID_MASK;

import java.io.IOException;
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
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyNode;
import uk.co.saiman.comms.copley.impl.CopleyControllerImpl.CopleyControllerConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.messaging.DataReceiver;
import uk.co.saiman.messaging.DataSender;

@Designate(ocd = CopleyControllerConfiguration.class, factory = true)
@Component(name = CopleyControllerImpl.CONFIGURATION_PID, configurationPid = CopleyControllerImpl.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class CopleyControllerImpl implements CopleyController {
  static final String CONFIGURATION_PID = "uk.co.saiman.comms.copley";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Copley Comms Configuration", description = "The configuration for the underlying serial comms for a Copley motor control")
  public @interface CopleyControllerConfiguration {}

  private final Log log;

  private final BundleContext context;
  private final ByteConverterService converters;

  private final DataSender sender;
  private final DataReceiver receiver;

  private Map<CopleyNodeImpl, ServiceRegistration<CopleyNode>> nodes = new LinkedHashMap<>();
  private Map<CopleyAxis, ServiceRegistration<CopleyAxis>> axes = new LinkedHashMap<>();

  @Activate
  public CopleyControllerImpl(
      BundleContext context,
      @Reference ByteConverterService converters,
      @Reference(name = "command") DataSender sender,
      @Reference(name = "response") DataReceiver receiver,
      @Reference Log log)
      throws IOException {
    this.context = context;
    this.converters = converters;

    this.sender = sender;
    this.receiver = receiver;

    this.log = log;
  }

  DataSender getSender() {
    return sender;
  }

  DataReceiver getReceiver() {
    return receiver;
  }

  Log getLog() {
    return log;
  }

  protected void open() {
    if (nodes.isEmpty() || axes.isEmpty()) {
      try {
        log.log(Level.INFO, "Open copley controller");

        List<CopleyNodeImpl> nodes = listNodes();

        nodes.forEach(node -> {
          this.nodes
              .put(node, context.registerService(CopleyNode.class, node, getProperties(node)));

          node.getAxes().forEach(axis -> {
            this.axes
                .put(axis, context.registerService(CopleyAxis.class, axis, getProperties(axis)));
          });
        });
      } catch (Exception e) {
        log.log(Level.ERROR, "Failed to open copley controller", e);
      }
    }
  }

  private Dictionary<String, String> getProperties(CopleyNode node) {
    Dictionary<String, String> properties = new Hashtable<>();
    return properties;
  }

  private Dictionary<String, String> getProperties(CopleyAxis axis) {
    Dictionary<String, String> properties = new Hashtable<>();
    return properties;
  }

  protected void close() {
    log.log(Level.INFO, "Close copley controller");

    nodes.values().forEach(n -> n.unregister());
    nodes.clear();
    axes.values().forEach(n -> n.unregister());
    axes.clear();
  }

  protected void checkChannel() throws IOException {
    open();
    for (var node : nodes.keySet()) {
      node.ping();
    }
  }

  private List<CopleyNodeImpl> listNodes() throws IOException {
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

  ByteConverterService getConverters() {
    return converters;
  }

  @Override
  public Stream<CopleyNode> getNodes() {
    open();
    return upcastStream(nodes.keySet().stream());
  }

  @Override
  public void reset() {
    close();
    open();
  }
}
