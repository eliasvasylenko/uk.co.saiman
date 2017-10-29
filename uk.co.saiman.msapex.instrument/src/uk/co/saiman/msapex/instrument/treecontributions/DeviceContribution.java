package uk.co.saiman.msapex.instrument.treecontributions;

import static uk.co.saiman.eclipse.treeview.DefaultTreeCellContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.DefaultTreeCellContribution.setSupplemental;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.DeviceConnection;

@Component(scope = ServiceScope.PROTOTYPE)
public class DeviceContribution implements TreeContribution {
  @AboutToShow
  public void prepare(HBox node, TreeEntry<Device> item) {
    DeviceConnection state = item.data().connectionState().get();

    configurePseudoClass(node);
    configurePseudoClass(node, state.toString());

    setLabel(node, item.data().getName());
    setSupplemental(node, state.toString());
  }
}
