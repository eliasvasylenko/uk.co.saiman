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
 * This file is part of uk.co.saiman.webmodules.extender.
 *
 * uk.co.saiman.webmodules.extender is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.extender is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.extender.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static uk.co.saiman.webmodule.WebModuleConstants.ID_ATTRIBUTE;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION_ATTRIBUTE;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

import uk.co.saiman.webmodule.WebModule;

public class WebModuleCapabilities {
  private final Bundle bundle;
  private final WebModuleDependencies dependencies;
  private Collection<ServiceRegistration<WebModule>> registrations;

  public WebModuleCapabilities(Bundle bundle) {
    this.bundle = bundle;
    this.dependencies = new WebModuleDependencies(bundle, this::requirementsUpdatedListener);

    this.registrations = emptyList();

    update();
  }

  public void dispose() {
    unregister();
    dependencies.dispose();
  }

  public void update() {
    unregister();
    dependencies.update();
  }

  private void unregister() {
    synchronized (registrations) {
      registrations.forEach(ServiceRegistration::unregister);
      registrations = emptyList();
    }
  }

  private void requirementsUpdatedListener() {
    Optional<List<WebModule>> dependencies = this.dependencies.getWebModules();

    synchronized (registrations) {
      unregister();

      dependencies.ifPresent(d -> {
        registrations = bundle
            .adapt(BundleWiring.class)
            .getCapabilities(SERVICE_NAMESPACE)
            .stream()
            .filter(WebModuleCapabilities::isExtenderCapability)
            .map(capability -> new WebModuleImpl(capability, d))
            .map(module -> registerWebModule(module, bundle))
            .collect(toList());
      });
    }
  }

  private static boolean isExtenderCapability(org.osgi.resource.Capability capability) {
    return capability.getAttributes().get(EXTENDER_VERSION_ATTRIBUTE) != null;
  }

  private ServiceRegistration<WebModule> registerWebModule(WebModule module, Bundle bundle) {
    return bundle
        .getBundleContext()
        .registerService(WebModule.class, module, getProperties(module));
  }

  private Dictionary<String, Object> getProperties(WebModule module) {
    Dictionary<String, Object> properties = new Hashtable<>();

    properties.put(ID_ATTRIBUTE, module.id().toString());
    properties.put(VERSION_ATTRIBUTE, module.version());

    return properties;
  }
}
