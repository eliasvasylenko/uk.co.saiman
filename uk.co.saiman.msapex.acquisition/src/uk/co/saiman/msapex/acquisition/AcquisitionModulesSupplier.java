package uk.co.saiman.msapex.acquisition;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import uk.co.saiman.eclipse.ObservableListSupplier;
import uk.co.saiman.instrument.acquisition.AcquisitionModule;

@Component(service = ExtendedObjectSupplier.class, property = "dependency.injection.annotation=uk.co.saiman.msapex.acquisition.AcquisitionModules")
public class AcquisitionModulesSupplier extends ObservableListSupplier<AcquisitionModule> {
	@Override
	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addItem(AcquisitionModule item) {
		super.addItem(item);
	}

	@Override
	public void removeItem(AcquisitionModule item) {
		super.removeItem(item);
	}
}
