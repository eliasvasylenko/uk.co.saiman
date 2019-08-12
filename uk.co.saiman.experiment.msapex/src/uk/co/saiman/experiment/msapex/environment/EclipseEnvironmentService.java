package uk.co.saiman.experiment.msapex.environment;

import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;

import java.util.Optional;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.msapex.environment.EclipseEnvironmentService.SharedResourcesConfiguration;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.osgi.ServiceIndex;

@Designate(ocd = SharedResourcesConfiguration.class, factory = true)
@Component(
    configurationPid = EclipseEnvironmentService.CONFIGURATION_PID,
    configurationPolicy = OPTIONAL,
    enabled = true,
    immediate = true,
    service = { EclipseEnvironmentService.class })
public class EclipseEnvironmentService {
  static final String CONFIGURATION_PID = "uk.co.saiman.experiment.msapex.environment";

  public static final String ENVIRONMENT_ID_KEY = "environment-id";
  public static final String PROVISION_ID_KEY = "provision-id";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "MSApex Shared Environment Configuration",
      description = "A service to provide a shared experiment environment based on provision ids")
  public @interface SharedResourcesConfiguration {
    String targetPerspectiveId();

    String sharedResourceFilter() default "(" + PROVISION_ID_KEY + "=*)";
  }

  private final SharedResourcesConfiguration configuration;
  private final ServiceIndex<Object, String, Object> resourceIndex;

  @Activate
  public EclipseEnvironmentService(
      SharedResourcesConfiguration configuration,
      BundleContext context) throws InvalidSyntaxException {
    this.configuration = configuration;

    resourceIndex = ServiceIndex
        .open(
            context,
            FrameworkUtil.createFilter(configuration.sharedResourceFilter()),
            Function.identity(),
            (a, b) -> resourceIndexer(b).stream());
  }

  @Deactivate
  public void deactivate() {
    resourceIndex.close();
  }

  private static Optional<String> resourceIndexer(ServiceReference<?> serviceReference) {
    return Optional.ofNullable((String) serviceReference.getProperty(PROVISION_ID_KEY));
  }

  public String getTargetPerspectiveId() {
    return configuration.targetPerspectiveId();
  }

  public Observable<EclipseEnvironmentService> events() {
    return resourceIndex.events().map(e -> this);
  }
}
