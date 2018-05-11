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
package uk.co.saiman.comms.impl;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.webmodule.react.RequireReactWebModule;
import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.webconsole.RequireSAIWebConsoleWebModule;

/*
 * TODO generate requirement annotations in webbundle bundles
 */
// @RequirePropTypesWebResource 
@RequireSAIWebConsoleWebModule
@RequireReactWebModule
// @RequireReduxWebResource
// @RequireAxiosWebModule
// @RequireReduxThunkWebResource
// @RequireRequireJSWebResource
// @RequireReactWebResource
// @RequireReactDOMWebResource
// @RequireReactReduxWebResource
@Component
public class CommsWebConsolePluginManager {
  static final String WEBCONSOLE_LABEL = "felix.webconsole.label";

  private final Set<CommsREST> restClasses = new HashSet<>();
  private final Map<CommsREST, ServiceRegistration<Servlet>> serviceRegistrations = new HashMap<>();
  private BundleContext context;

  @Activate
  synchronized void activate(BundleContext context) {
    this.context = context;
    restClasses.stream().forEach(this::register);
  }

  void register(CommsREST comms) {
    CommsWebConsolePlugin plugin = new CommsWebConsolePlugin(comms.getID(), comms.getName());
    Dictionary<String, Object> properties = new Hashtable<>();
    properties.put(WEBCONSOLE_LABEL, comms.getID());

    ServiceRegistration<Servlet> registration = context
        .registerService(Servlet.class, plugin, properties);
    plugin.activate(context);

    serviceRegistrations.put(comms, registration);
  }

  @Reference(policy = DYNAMIC, cardinality = MULTIPLE)
  synchronized void addComms(CommsREST comms) {
    try {
      restClasses.add(comms);

      if (context != null) {
        register(comms);
      }
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  synchronized void removeComms(CommsREST comms) {
    restClasses.remove(comms);
    ServiceRegistration<?> registration = serviceRegistrations.remove(comms);

    if (registration != null) {
      ((CommsWebConsolePlugin) context.getService(registration.getReference())).deactivate();
      registration.unregister();
    }
  }
}
