package uk.co.saiman.experiment.msapex.impl;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.experiment.spectrum.SpectrumConfiguration;

@Component(immediate = true)
public class SpectrumConfigurationAdapterFactory implements IAdapterFactory {
	@Reference
	private IAdapterManager adapterManager;

	@Activate
	public void register() {
		adapterManager.registerAdapters(this, SpectrumConfiguration.class);
	}

	@Deactivate
	public void unregister() {
		adapterManager.unregisterAdapters(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		SpectrumConfiguration configuration = (SpectrumConfiguration) adaptableObject;

		if (adapterType == AcquisitionDevice.class) {
			return (T) configuration.getAcquisitionDevice();
		}

		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] { AcquisitionDevice.class };
	}
}
