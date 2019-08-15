package uk.co.saiman.experiment.msapex.environment;

import static java.util.function.Function.identity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.core.di.extensions.Service;
import org.osgi.framework.BundleContext;

import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.osgi.ServiceIndex;

public class ResourceService {
  @Inject
  private IEclipseContext context;

  @Inject
  @OSGiBundle
  private BundleContext bundleContext;

  @Inject
  @Service
  private LocalEnvironmentService localEnvironmentService;

  private ServiceIndex<GlobalEnvironment, String, GlobalEnvironment> sharedResources;
  private final Map<Class<?>, ResourcePresenter<?>> resourcePresenters = new HashMap<>();

  @PostConstruct
  void initialize() {
    sharedResources = ServiceIndex
        .open(
            bundleContext,
            GlobalEnvironment.class,
            identity(),
            (e, s) -> Optional
                .ofNullable(s.getProperty("target-perspective-id").toString())
                .stream());
  }

  /*
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * TODO shared resources are identified directly from their service
   * registrations via SharedEnvironmentService. The resources are then correlated
   * with the associated Provision objects and a SharedEnvironment is created from
   * the service, and injected into the context of the appropriate perspective.
   * The environment is recreated and reinjected each time a provision is
   * registered or the service changes.
   * 
   * 
   * Local resources are created from shared ones according to some function. We
   * don't need to know which local resources will be available until we try to
   * acquire them and either succeed or fail.
   * 
   * 
   * 
   * 
   * TODO open question as to whether we register ResourcePresentations
   * programmatically via this service injected into an addon, or simply by
   * registering a ResourcePresentation service in the framework. The latter
   * option probably is better and more closely aligns with how DevicePresentation
   * is likely to work.
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   */
}
