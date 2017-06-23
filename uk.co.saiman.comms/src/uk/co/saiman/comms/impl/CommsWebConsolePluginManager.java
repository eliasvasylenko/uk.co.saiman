/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
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
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.facebook.react.RequireReactWebResource;
import uk.co.saiman.facebook.react.dom.RequireReactDOMWebResource;
import uk.co.saiman.facebook.react.redux.RequireReactReduxWebResource;
import uk.co.saiman.redux.RequireReduxWebResource;
import uk.co.saiman.requirejs.RequireRequireJSWebResource;
import uk.co.saiman.webconsole.RequireSAIWebConsoleWebResource;

@RequireSAIWebConsoleWebResource
@RequireReactWebResource
@RequireReduxWebResource
@RequireRequireJSWebResource
@RequireReactDOMWebResource
@RequireReactReduxWebResource
@Component
public class CommsWebConsolePluginManager {
	static final String WEBCONSOLE_LABEL = "felix.webconsole.label";

	private final Map<CommsREST, CommsWebConsolePlugin> restClasses = new HashMap<>();
	private final Map<Servlet, ServiceRegistration<Servlet>> serviceRegistrations = new HashMap<>();
	private BundleContext context;

	@Activate
	synchronized void activate(BundleContext context) {
		this.context = context;
		restClasses.entrySet().stream().forEach(e -> register(e.getKey(), e.getValue()));
	}

	void register(CommsREST comms, CommsWebConsolePlugin plugin) {
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(WEBCONSOLE_LABEL, comms.getID());

		ServiceRegistration<Servlet> registration = context
				.registerService(Servlet.class, plugin, properties);
		plugin.activate(context);

		serviceRegistrations.put(plugin, registration);
	}

	@Reference(policy = DYNAMIC, cardinality = MULTIPLE)
	synchronized void addComms(CommsREST comms) {
		try {
			CommsWebConsolePlugin restService = new CommsWebConsolePlugin(comms.getID(), comms.getName());
			restClasses.put(comms, restService);

			if (context != null) {
				register(comms, restService);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	synchronized void removeComms(CommsREST comms) {
		CommsWebConsolePlugin plugin = restClasses.remove(comms);
		ServiceRegistration<?> registration = serviceRegistrations.remove(plugin);

		if (registration != null) {
			registration.unregister();
			plugin.deactivate();
		}
	}
}
