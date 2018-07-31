package uk.co.saiman.msapex.experiment;

import java.util.Optional;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.ResultStorage;
import uk.co.saiman.msapex.experiment.locations.ResultStorageProvider;

@Component
public class WorkspaceResultStorageProvider implements ResultStorageProvider {
  @Override
  public String getName() {
    return "Workspace Path";
  }

  @Override
  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<ResultStorage> requestLocator(IEclipseContext context, String experimentName) {
    return Optional.of(ContextInjectionFactory.make(WorkspaceResultLocator.class, context));
  }
}
