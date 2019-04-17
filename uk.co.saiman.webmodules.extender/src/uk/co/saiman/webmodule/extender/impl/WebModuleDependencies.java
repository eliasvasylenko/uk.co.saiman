/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.stream.Collectors.toList;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION_ATTRIBUTE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import uk.co.saiman.webmodule.WebModule;

class WebModuleDependencies {
  private final Bundle bundle;
  private final Runnable refresh;
  private Map<BundleWire, WebModuleWires> wires;

  public WebModuleDependencies(Bundle bundle, Runnable refresh) {
    this.bundle = bundle;
    this.refresh = refresh;
    this.wires = new HashMap<>();
  }

  private static boolean isExtenderRequirement(BundleWire requirement) {
    return requirement.getRequirement().getAttributes().get(EXTENDER_VERSION_ATTRIBUTE) != null;
  }

  public void dispose() {
    synchronized (wires) {
      wires.values().forEach(WebModuleWires::dispose);
      wires.clear();
    }
  }

  public void update() {
    boolean fulfilled;

    synchronized (wires) {
      dispose();
      bundle
          .adapt(BundleWiring.class)
          .getRequiredWires(SERVICE_NAMESPACE)
          .stream()
          .filter(WebModuleDependencies::isExtenderRequirement)
          .map(wire -> new WebModuleWires(wire, refresh))
          .forEach(wires -> this.wires.put(wires.getWire(), wires));

      fulfilled = wires.isEmpty();
    }

    if (fulfilled) {
      refresh.run();
    }
  }

  public Optional<List<WebModule>> getWebModules() {
    synchronized (wires) {
      boolean fulfilled = wires
          .values()
          .stream()
          .map(WebModuleWires::getReference)
          .allMatch(Optional::isPresent);

      if (fulfilled) {
        return Optional
            .ofNullable(
                wires
                    .values()
                    .stream()
                    .map(WebModuleWires::getReference)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toList()));
      } else {
        return Optional.empty();
      }
    }
  }
}
