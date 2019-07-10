package uk.co.saiman.experiment.service.impl;

import static org.osgi.framework.FrameworkUtil.createFilter;
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.environment.EnvironmentService;
import uk.co.saiman.experiment.environment.Provision;
import uk.co.saiman.experiment.environment.StaticEnvironment;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.service.ResourceProvider;
import uk.co.saiman.experiment.service.impl.EnvironmentServiceImpl.EnvironmentServiceConfiguration;
import uk.co.saiman.osgi.ServiceIndex;

@Designate(ocd = EnvironmentServiceConfiguration.class, factory = true)
@Component(configurationPid = EnvironmentServiceImpl.CONFIGURATION_PID, configurationPolicy = OPTIONAL)
public class EnvironmentServiceImpl implements EnvironmentService {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Environment Service", description = "A service over a set of resource providers over which an environment can be opened")
  public @interface EnvironmentServiceConfiguration {
    String resourceProviderFilter() default "";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.environment";

  private final ServiceIndex<?, Provision<?>, ResourceProvider<?>> resourceProviders;

  @Activate
  public EnvironmentServiceImpl(
      EnvironmentServiceConfiguration configuration,
      BundleContext context)
      throws InvalidSyntaxException {
    String filterString = "(" + Constants.OBJECTCLASS + "=" + Executor.class.getName() + ")";
    if (!configuration.resourceProviderFilter().isBlank()) {
      filterString = "(&" + filterString + configuration.resourceProviderFilter() + ")";
    }
    resourceProviders = ServiceIndex
        .open(
            context,
            createFilter(filterString),
            Function.identity(),
            EnvironmentServiceImpl::providerIndexer);
  }

  private static Optional<Provision<?>> providerIndexer(
      ResourceProvider<?> object,
      ServiceReference<ResourceProvider<?>> serviceReference) {
    return Optional.of(object.provision());
  }

  @Override
  public Environment openEnvironment(
      Collection<? extends Provision<?>> provisions,
      long timeout,
      TimeUnit unit) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StaticEnvironment getStaticEnvironment() {
    // TODO Auto-generated method stub
    return null;
  }
}
