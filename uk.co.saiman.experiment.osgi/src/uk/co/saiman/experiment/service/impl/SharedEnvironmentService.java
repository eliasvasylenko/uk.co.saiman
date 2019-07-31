package uk.co.saiman.experiment.service.impl;

import static java.util.stream.Collectors.toList;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.environment.ResourceMissingException;
import uk.co.saiman.experiment.environment.ResourceUnavailableException;
import uk.co.saiman.experiment.environment.SharedEnvironment;
import uk.co.saiman.experiment.service.LimitedResourceProvider;
import uk.co.saiman.experiment.service.UnlimitedResourceProvider;
import uk.co.saiman.experiment.service.impl.SharedEnvironmentService.ResourceServiceConfiguration;

@Designate(ocd = ResourceServiceConfiguration.class, factory = true)
@Component(configurationPid = SharedEnvironmentService.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class SharedEnvironmentService implements SharedEnvironment {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Resource Service", description = "A service over a set of resource providers over which an environment can be opened")
  public @interface ResourceServiceConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.environment";

  private final Map<Provision<?>, LimitedResourceProvider> limitedResourceProviders;
  private final Map<LimitedResourceProvider, Provision<?>> limitedResourceProviderIds;
  private final Map<Provision<?>, UnlimitedResourceProvider> unlimitedResourceProviders;
  private final Map<UnlimitedResourceProvider, Provision<?>> unlimitedResourceProviderIds;

  @Activate
  public SharedEnvironmentService(
      BundleContext context,
      @Reference(name = "limitedResources", policyOption = GREEDY) List<ServiceReference<LimitedResourceProvider>> limitedResources,
      @Reference(name = "unlimitedResources", policyOption = GREEDY) List<ServiceReference<UnlimitedResourceProvider>> unlimitedResources) {
    this.limitedResourceProviders = new HashMap<>();
    this.limitedResourceProviderIds = new HashMap<>();

    for (var resourceReference : limitedResources) {
      var resource = context.getService(resourceReference);
      resource.limitedProvisions().forEach(id -> {
        this.limitedResourceProviders.put(id, resource);
        this.limitedResourceProviderIds.put(resource, id);
      });
    }

    this.unlimitedResourceProviders = new HashMap<>();
    this.unlimitedResourceProviderIds = new HashMap<>();

    for (var resourceReference : unlimitedResources) {
      var resource = context.getService(resourceReference);
      resource.unlimitedProvisions().forEach(id -> {
        this.unlimitedResourceProviders.put(id, resource);
        this.unlimitedResourceProviderIds.put(resource, id);
      });
    }
  }

  @Override
  public Stream<Provision<?>> providedValues() {
    return unlimitedResourceProviders.keySet().stream();
  }

  @Override
  public boolean providesValue(Provision<?> provision) {
    return unlimitedResourceProviders.containsKey(provision);
  }

  @Override
  public <T> T provideValue(Provision<T> provision) {
    var provider = unlimitedResourceProviders.get(provision);
    if (provider == null) {
      throw new ResourceMissingException(provision);
    }
    var value = provider.provideValue(provision);
    if (value == null) {
      throw new ResourceUnavailableException(provision, new NullPointerException());
    }
    return value;
  }

  @Override
  public LocalEnvironment openLocalEnvironment(
      Collection<? extends Provision<?>> resources,
      long timeout,
      TimeUnit unit) {
    return new LocalEnvironment() {
      @Override
      public void close() throws Exception {
        // TODO Auto-generated method stub

      }

      @Override
      public boolean providesResource(Provision<?> provision) {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public Stream<Provision<?>> providedResources() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public <T> Resource<T> provideResource(Provision<T> provision) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public SharedEnvironment getSharedEnvironment() {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
}
