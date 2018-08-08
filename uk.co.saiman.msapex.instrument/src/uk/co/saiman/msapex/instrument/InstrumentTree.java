package uk.co.saiman.msapex.instrument;

import static org.osgi.service.component.ComponentConstants.COMPONENT_NAME;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.eclipse.ui.model.MCell;
import uk.co.saiman.eclipse.ui.model.MCellImpl;
import uk.co.saiman.eclipse.ui.model.MTree;
import uk.co.saiman.eclipse.ui.model.MTreeImpl;

@Component(name = InstrumentTree.ID, service = MTree.class)
public class InstrumentTree extends MTreeImpl {
  public static final String ID = "uk.co.saiman.instrument.tree";
  public static final String INSTRUMENT_ID = ID + ".root";

  public InstrumentTree() {
    super(ID, Contribution.class);
  }

  @Reference(target = "(" + COMPONENT_NAME + "=" + InstrumentCell.ID + ")")
  public void setRoot(MCell instrument) {
    MCellImpl child = new MCellImpl(INSTRUMENT_ID, null);
    child.setSpecialized(instrument);
    super.setRootCell(child);
  }

  public static class Contribution {}
}
