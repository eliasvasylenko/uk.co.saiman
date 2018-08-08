package uk.co.saiman.msapex.experiment.spectrum;

import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.eclipse.ui.model.MTree;
import uk.co.saiman.eclipse.ui.model.MTreeImpl;
import uk.co.saiman.msapex.experiment.processing.treecontributions.ProcessorListCell;

@Component(name = ProcessingTree.ID, service = MTree.class)
public class ProcessingTree extends MTreeImpl {
  public static final String ID = "uk.co.saiman.experiment.processing";
  public static final String PROCESSING_LIST_ID = ID + ".list";

  public ProcessingTree() {
    super(ID, Contribution.class);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + ProcessorListCell.ID + ")")
  public void setRoot(MCell instrument) {
    MCellImpl child = new MCellImpl(PROCESSING_LIST_ID, null);
    child.setSpecialized(instrument);
    super.setRootCell(child);
  }

  public static class Contribution {}
}
