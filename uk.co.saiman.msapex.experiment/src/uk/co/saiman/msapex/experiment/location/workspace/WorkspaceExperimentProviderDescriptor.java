package uk.co.saiman.msapex.experiment.location.workspace;

import static uk.co.saiman.experiment.storage.filesystem.FileSystemStore.FILE_SYSTEM_STORE_ID;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.msapex.experiment.location.ExperimentProvider;
import uk.co.saiman.msapex.experiment.location.AddExperimentWizard;

@Component
public class WorkspaceExperimentProviderDescriptor implements AddExperimentWizard {

  @Override
  public String getLabel() {
    return "New Workspace Experiment"; // TODO l10n
  }

  @Override
  public String getId() {
    return FILE_SYSTEM_STORE_ID;
  }

  @Override
  public Class<? extends ExperimentProvider<?>> getFirstPage() {
    return WorkspaceExperimentProvider.class;
  }

  @Override
  public String getIconURI() {
    // TODO Auto-generated method stub
    return null;
  }
}
