package uk.co.saiman.msapex.instrument.treecontributions;

import static uk.co.saiman.reflection.token.TypedReference.typedObject;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import uk.co.saiman.eclipse.treeview.ModularTreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.reflection.token.TypedReference;

@Component(scope = ServiceScope.PROTOTYPE)
public class InstrumentContribution implements ModularTreeContribution {
  @AboutToShow
  public void prepare(TreeEntry<Instrument> data, List<TypedReference<?>> children) {
    data.data().getDevices().map(c -> typedObject(Device.class, c)).forEach(children::add);
  }
}
