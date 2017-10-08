package uk.co.saiman.msapex.instrument.treecontributions;

import static uk.co.saiman.eclipse.treeview.DefaultTreeCellContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.DefaultTreeCellContribution.setSupplemental;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.ModularTreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.instrument.Device;

@Component(scope = ServiceScope.PROTOTYPE)
public class DeviceContribution implements ModularTreeContribution {
  @AboutToShow
  public void prepare(HBox node, TreeEntry<Device> item) {
    configurePseudoClass(node);
    configurePseudoClass(node, item.data().connectionState().get().toString());

    setLabel(node, item.data().getName());
    setSupplemental(node, item.data().connectionState().get().toString());
  }
}
