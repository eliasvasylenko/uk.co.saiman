/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.processing.experiment.
 *
 * uk.co.saiman.processing.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.processing.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
