package uk.co.saiman.msapex.experiment;

import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.eclipse.ui.model.MTree;
import uk.co.saiman.eclipse.ui.model.MTreeImpl;
import uk.co.saiman.msapex.experiment.treecontributions.WorkspaceCell;

@Component(name = ExperimentTree.ID, service = MTree.class)
public class ExperimentTree extends MTreeImpl {
  public static final String ID = "uk.co.saiman.experiment";
  public static final String WORKSPACE_ID = ID + ".workspace";

  public ExperimentTree() {
    super(ID, Contribution.class);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + WorkspaceCell.ID + ")")
  public void setRoot(MCell instrument) {
    MCellImpl child = new MCellImpl(WORKSPACE_ID, null);
    child.setSpecialized(instrument);
    super.setRootCell(child);
  }

  public static class Contribution {}
}
