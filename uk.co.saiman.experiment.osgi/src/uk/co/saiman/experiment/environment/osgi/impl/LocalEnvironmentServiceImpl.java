package uk.co.saiman.experiment.environment.osgi.impl;

import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.environment.osgi.ExclusiveResourceProvider;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;

@Component(enabled = true, immediate = true)
public class LocalEnvironmentServiceImpl implements LocalEnvironmentService {
  @Activate
  public LocalEnvironmentServiceImpl(
      @Reference(
          name = "exclusiveResourceProviders",
          policyOption = GREEDY) List<ExclusiveResourceProvider<?>> exclusiveResourceProviders) {}

  @Override
  public LocalEnvironment openLocalEnvironment(
      GlobalEnvironment globalEnvironment,
      Collection<? extends Provision<?>> resources,
      long timeout,
      TimeUnit unit) {
    // TODO Auto-generated method stub
    return null;
  }
}
