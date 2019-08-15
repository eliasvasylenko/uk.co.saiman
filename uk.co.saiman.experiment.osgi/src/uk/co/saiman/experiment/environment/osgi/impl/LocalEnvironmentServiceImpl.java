package uk.co.saiman.experiment.environment.osgi.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.environment.ResourceMissingException;
import uk.co.saiman.experiment.environment.ResourceUnavailableException;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;
import uk.co.saiman.experiment.environment.osgi.impl.LocalEnvironmentServiceImpl.LocalEnvironmentServiceConfiguration;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;

@Designate(ocd = LocalEnvironmentServiceConfiguration.class, factory = true)
@Component(
    configurationPid = LocalEnvironmentServiceImpl.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL)
public class LocalEnvironmentServiceImpl implements LocalEnvironmentService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Local Environment Service",
      description = "A service for management of local experiment environments for exclusive resources")
  public @interface LocalEnvironmentServiceConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.environment.local";

  private final Map<Class<?>, ExclusiveResourceProvider<?>> executors;
  private final Map<ExclusiveResourceProvider<?>, Class<?>> ids;

  @Activate
  public LocalEnvironmentServiceImpl(
      BundleContext context,
      @Reference(
          name = "exclusiveResourceProviders",
          policyOption = GREEDY) List<ExclusiveResourceProvider<?>> exclusiveResourceProviders) {
    this.executors = new HashMap<>();
    this.ids = new HashMap<>();

    for (var resourceProvider : exclusiveResourceProviders) {
      serviceProviderIndexer(resourceProvider).forEach(id -> {
        this.executors.put(id, resourceProvider);
        this.ids.put(resourceProvider, id);
      });
    }
  }

  private Stream<Class<?>> serviceProviderIndexer(ExclusiveResourceProvider<?> provider) {
    return Stream.of(provider.getProvision());
  }

  @Override
  public LocalEnvironment openLocalEnvironment(
      GlobalEnvironment globalEnvironment,
      Collection<? extends Class<?>> resources,
      long timeout,
      TimeUnit unit) {
    Map<Class<?>, Resource<?>> resourceMap = new HashMap<>();

    resources
        .stream()
        .map(c -> getResource((Class<?>) c, globalEnvironment, timeout, unit))
        .forEach(r -> resourceMap.put(r.type(), r));

    return new LocalEnvironment() {
      @Override
      public void close() throws Exception {
        resourceMap.values().forEach(Resource::close);
      }

      @Override
      public Stream<Class<?>> providedResources() {
        return resourceMap.keySet().stream();
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> Resource<T> provideResource(Class<T> provision) {
        return (Resource<T>) resourceMap.get(provision);
      }

      @Override
      public GlobalEnvironment getGlobalEnvironment() {
        return globalEnvironment;
      }
    };
  }

  private <T> Resource<T> getResource(
      Class<T> type,
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) {
    if (globalEnvironment.providesValue(type)) {
      return Resource.over(type, globalEnvironment.provideValue(type), t -> () -> {});
    } else if (executors.containsKey(type)) {
      try {
        @SuppressWarnings("unchecked")
        var exclusiveResource = ((ExclusiveResourceProvider<T>) executors.get(type))
            .deriveResource(globalEnvironment, timeout, unit);
        return Resource.over(type, exclusiveResource.getValue(), e -> exclusiveResource);
      } catch (Exception e) {
        throw new ResourceUnavailableException(type, e);
      }
    } else {
      throw new ResourceMissingException(type);
    }
  }
}
