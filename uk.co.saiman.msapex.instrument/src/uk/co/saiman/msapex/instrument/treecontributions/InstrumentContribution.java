package uk.co.saiman.msapex.instrument.treecontributions;

import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import uk.co.saiman.fx.TreeChildContribution;
import uk.co.saiman.fx.TreeContribution;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.reflection.token.TypedReference;

@Component(service = TreeContribution.class, scope = ServiceScope.PROTOTYPE)
public class InstrumentContribution implements TreeChildContribution<Instrument> {
  @Override
  public <U extends Instrument> Stream<TypedReference<?>> getChildren(TreeItemData<U> data) {
    return data.data().getDevices().map(c -> TypedReference.typedObject(Device.class, c));
  }
}
