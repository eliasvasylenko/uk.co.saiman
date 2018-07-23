package uk.co.saiman.msapex.experiment;

import static org.eclipse.e4.ui.internal.workbench.E4Workbench.INSTANCE_LOCATION;

import java.io.IOException;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Creatable;

import uk.co.saiman.data.resource.Location;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ResultStorage;

@Creatable
public class WorkspaceResultLocator implements ResultStorage {
  @Named(INSTANCE_LOCATION)
  Location instanceLocation;

  @Override
  public void removeLocation(ExperimentNode<?, ?> node) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateLocation(ExperimentNode<?, ?> node, String id) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public Location getLocation(ExperimentNode<?, ?> node) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

}
