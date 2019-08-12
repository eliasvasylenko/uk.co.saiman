package uk.co.saiman.properties.provider;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleWiring;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.properties.service.PropertiesServiceConstants;

public class PropertiesCapabilities {
  private final Bundle bundle;
  private final PropertyLoader loader;
  private final Log log;
  private final Version version;

  private Collection<ServiceRegistration<?>> registrations;

  public PropertiesCapabilities(Bundle bundle, PropertyLoader loader, Log log, Version version) {
    this.bundle = bundle;
    this.loader = loader;
    this.log = log;
    this.version = version;

    this.registrations = emptyList();

    update();
  }

  public void update() {
    synchronized (registrations) {
      unregister();

      registrations = bundle
          .adapt(BundleWiring.class)
          .getCapabilities(SERVICE_NAMESPACE)
          .stream()
          .flatMap(this::registerProperties)
          .collect(toList());
    }
  }

  private Stream<? extends ServiceRegistration<?>> registerProperties(
      org.osgi.resource.Capability capability) {
    if (capability.getAttributes().get(PropertiesServiceConstants.EXTENDER_NAME) == null) {
      return Stream.empty();
    }
    Object objectClass = capability.getAttributes().get(Constants.OBJECTCLASS);
    if (objectClass == null) {
      log.log(Level.ERROR, "Object class not found");
      return Stream.empty();
    }
    List<String> objectClasses;
    if (objectClass instanceof Object[]) {
      objectClasses = Stream.of((Object[]) objectClass).map(Objects::toString).collect(toList());
    } else if (objectClass instanceof Collection<?>) {
      objectClasses = ((Collection<?>) objectClass)
          .stream()
          .map(Objects::toString)
          .collect(toList());
    } else {
      objectClasses = List.of(objectClass.toString());
    }

    List<Class<?>> propertiesClass = objectClasses.stream().flatMap(c -> {
      try {
        return Stream.of(bundle.adapt(BundleWiring.class).getClassLoader().loadClass(c));
      } catch (Exception e) {
        log.log(Level.ERROR, "Object class not found ", e);
        return Stream.empty();
      }
    }).collect(toList());

    return propertiesClass
        .stream()
        .flatMap(c -> registerPropertiesClass(c, capability.getAttributes()).stream());
  }

  private <T> Optional<ServiceRegistration<T>> registerPropertiesClass(
      Class<T> propertiesClass,
      Map<String, Object> attributes) {
    try {
      return Optional
          .of(
              bundle
                  .getBundleContext()
                  .registerService(
                      propertiesClass,
                      loader
                          .getProperties(
                              propertiesClass,
                              bundle.adapt(BundleWiring.class).getClassLoader()),
                      new Hashtable<>(attributes)));

    } catch (Exception e) {
      log.log(Level.ERROR, "Failed to register properties service ", e);
      return Optional.empty();
    }
  }

  public void dispose() {
    unregister();
  }

  private void unregister() {
    synchronized (registrations) {
      registrations.forEach(ServiceRegistration::unregister);
      registrations = emptyList();
    }
  }
}
