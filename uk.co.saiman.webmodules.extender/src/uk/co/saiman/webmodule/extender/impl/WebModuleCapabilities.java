package uk.co.saiman.webmodule.extender.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static uk.co.saiman.webmodule.WebModuleConstants.ID_ATTRIBUTE;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION_ATTRIBUTE;

import java.util.ArrayList;
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
    this.registrations = new ArrayList<>();

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
