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
