package uk.co.saiman.experiment.environment.osgi.impl;

import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.ResourceMissingException;
import uk.co.saiman.experiment.environment.ResourceUnavailableException;
import uk.co.saiman.experiment.environment.osgi.impl.GlobalEnvironmentService.GlobalEnvironmentServiceConfiguration;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.osgi.ServiceIndex;

@Designate(ocd = GlobalEnvironmentServiceConfiguration.class, factory = true)
@Component(
    configurationPid = GlobalEnvironmentService.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL,
    enabled = true,
    immediate = true)
public class GlobalEnvironmentService {
  public static final String PROVISION_ID_ATTRIBUTE = "provision-id";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Global Environment Service",
      description = "A service for management of global experiment environments for shared resources")
  public @interface GlobalEnvironmentServiceConfiguration {
    String sharedResourceFilter() default "(" + PROVISION_ID_ATTRIBUTE + "=*)";
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.environment.global";

  private final Log log;
  private final ServiceIndex<Object, Class<?>, Object> resourceIndex;
  private ServiceRegistration<GlobalEnvironment> serviceRegistration;

  @Activate
  public GlobalEnvironmentService(
      GlobalEnvironmentServiceConfiguration configuration,
      BundleContext context,
      @Reference Log log,
      Map<String, Object> properties) throws InvalidSyntaxException {
    this.log = log;

    resourceIndex = ServiceIndex
        .open(
            context,
            FrameworkUtil.createFilter(configuration.sharedResourceFilter()),
            Function.identity(),
            (a, b) -> resourceIndexer(b));
    resourceIndex.events().observe(o -> registerService(context, properties));
    registerService(context, properties);
  }

  @Deactivate
  public void deactivate() {
    resourceIndex.close();
  }

  private Stream<Class<?>> resourceIndexer(ServiceReference<?> serviceReference) {
    var classNames = (String[]) serviceReference.getProperty(OBJECTCLASS);
    var classLoader = serviceReference.getBundle().adapt(BundleWiring.class).getClassLoader();
    return Stream.of(classNames).flatMap(className -> {
      try {
        return Stream.of(classLoader.loadClass(className));
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        log.log(Level.ERROR, e);
        return Stream.empty();
      }
    });
  }

  private synchronized void registerService(BundleContext context, Map<String, Object> properties) {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }
    var values = new HashMap<Class<?>, Object>();

    resourceIndex
        .ids()
        .forEach(id -> values.put(id, resourceIndex.highestRankedRecord(id).get().serviceObject()));

    var globalEnvironment = new GlobalEnvironment() {
      @Override
      public Stream<Class<?>> providedValues() {
        return values.keySet().stream();
      }

      @Override
      public boolean providesValue(Class<?> provision) {
        return values.containsKey(provision);
      }

      @Override
      public <T> T provideValue(Class<T> provision) {
        if (!providesValue(provision)) {
          throw new ResourceMissingException(provision);
        }
        @SuppressWarnings("unchecked")
        var value = (T) values.get(provision);
        if (value == null) {
          throw new ResourceUnavailableException(provision, new NullPointerException());
        }
        return value;
      }
    };
    serviceRegistration = context
        .registerService(GlobalEnvironment.class, globalEnvironment, new Hashtable<>(properties));
  }
}
