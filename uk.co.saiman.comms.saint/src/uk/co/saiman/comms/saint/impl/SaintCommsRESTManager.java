package uk.co.saiman.comms.saint.impl;

import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.comms.saint.SaintComms;

@Component
public class SaintCommsRESTManager {
	private final Map<SaintComms, CommsREST> restClasses = new HashMap<>();
	private final Map<CommsREST, ServiceRegistration<CommsREST>> serviceRegistrations = new HashMap<>();
	private BundleContext context;

	@Reference
	private DTOs dtos;

	@Activate
	synchronized void activate(BundleContext context) {
		this.context = context;
		restClasses.entrySet().stream().forEach(e -> register(e.getKey(), e.getValue()));
	}

	void register(SaintComms comms, CommsREST rest) {
		serviceRegistrations.put(rest, context.registerService(CommsREST.class, rest, null));
	}

	@Reference(policy = DYNAMIC, policyOption = GREEDY)
	synchronized void addComms(SaintComms comms) {
		CommsREST restService = new SaintCommsREST(comms, dtos);
		restClasses.put(comms, restService);

		if (context != null) {
			register(comms, restService);
		}
	}

	synchronized void removeComms(SaintComms comms) {
		ServiceRegistration<?> restService = serviceRegistrations.remove(restClasses.remove(comms));
		if (restService != null) {
			restService.unregister();
		}
	}
}
