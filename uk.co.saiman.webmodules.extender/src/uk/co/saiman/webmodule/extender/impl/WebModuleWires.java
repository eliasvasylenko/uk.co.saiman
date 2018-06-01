package uk.co.saiman.webmodule.extender.impl;

import static org.osgi.framework.FrameworkUtil.createFilter;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.util.tracker.ServiceTracker;

import uk.co.saiman.webmodule.WebModule;

class WebModuleWires extends ServiceTracker<WebModule, WebModule> {
  private final BundleWire wire;
  private final Runnable refresh;
  private final SortedMap<ServiceReference<WebModule>, WebModule> references;

  public WebModuleWires(BundleWire wire, Runnable refresh) {
    super(getContext(wire), getFilter(wire), null);
    this.wire = wire;
    this.refresh = refresh;
    this.references = new TreeMap<>();

    open();
  }

  private static BundleContext getContext(BundleWire wire) {
    return wire.getRequirer().getBundle().getBundleContext();
  }

  private static Filter getFilter(BundleWire wire) {
    try {
      String filterString = wire.getRequirement().getDirectives().get("filter");
      return createFilter(filterString);
    } catch (InvalidSyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private boolean isFromCapability(ServiceReference<WebModule> reference) {
    return wire.getCapability().getResource().getBundle() == reference.getBundle();
  }

  @Override
  public WebModule addingService(ServiceReference<WebModule> reference) {
    boolean changed = references.isEmpty();

    WebModule service;

    synchronized (references) {
      if (isFromCapability(reference)) {
        service = context.getService(reference);
        references.put(reference, service);
      } else {
        service = null;
        changed = false;
      }
    }

    if (changed) {
      refresh.run();
    }

    return service;
  }

  @Override
  public void removedService(ServiceReference<WebModule> reference, WebModule service) {
    synchronized (references) {
      references.remove(wire, service);
      context.ungetService(reference);
    }

    if (references.isEmpty()) {
      refresh.run();
    }
  }

  public Optional<WebModule> getReference() {
    synchronized (references) {
      return references.values().stream().findFirst();
    }
  }

  public BundleWire getWire() {
    return wire;
  }

  public void dispose() {
    close();
  }
}
