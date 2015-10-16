package uk.co.saiman.processing.experiment;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.processing.ConfigurableProcessor;
import uk.co.saiman.processing.Processor;

@Component
public class ProcessingExperimentManager {
	private BundleContext context;
	private final Map<Processor<?, ?>, ServiceRegistration<?>> processorServices;
	private final Map<ConfigurableProcessor<?, ?, ?>, ServiceRegistration<?>> configurableProcessorServices;

	public ProcessingExperimentManager() {
		processorServices = new HashMap<>();
		configurableProcessorServices = new HashMap<>();
	}

	@Activate
	public void activate(BundleContext context) {
		this.context = context;
	}

	@Reference
	public void addProcessor(Processor<?, ?> processor) {
		synchronized (processorServices) {
			if (!processorServices.containsKey(processor)) {
				processorServices.put(processor, context.registerService(Processor.class, processor, new Hashtable<>()));
			}
		}
	}

	public void removeProcessor(Processor<?, ?> processor) {
		synchronized (processorServices) {
			Optional.ofNullable(processorServices.remove(processor)).ifPresent(ServiceRegistration::unregister);
		}
	}

	@Reference
	public void addConfigurableProcessor(ConfigurableProcessor<?, ?, ?> processor) {
		synchronized (configurableProcessorServices) {
			if (!configurableProcessorServices.containsKey(processor)) {
				configurableProcessorServices.put(processor,
						context.registerService(ConfigurableProcessor.class, processor, new Hashtable<>()));
			}
		}
	}

	public void removeConfigurableProcessor(ConfigurableProcessor<?, ?, ?> processor) {
		synchronized (configurableProcessorServices) {
			Optional.ofNullable(configurableProcessorServices.remove(processor)).ifPresent(ServiceRegistration::unregister);
		}
	}
}
