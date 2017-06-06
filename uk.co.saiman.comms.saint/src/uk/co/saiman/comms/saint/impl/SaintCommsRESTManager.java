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

import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.comms.saint.SaintComms;

@Component
public class SaintCommsRESTManager {
	private final Map<SaintComms, ServiceRegistration<CommsREST>> restServices = new HashMap<>();
	private BundleContext context;

	@Activate
	void activate(BundleContext context) {
		this.context = context;
	}

	@Reference(policy = DYNAMIC, policyOption = GREEDY)
	void addComms(SaintComms comms) {
		CommsREST restService = new SaintCommsREST(comms, null);

		ServiceRegistration<CommsREST> registration = context
				.registerService(CommsREST.class, restService, null);

		restServices.put(comms, registration);
	}

	void removeComms(SaintComms comms) {
		ServiceRegistration<CommsREST> restService = restServices.remove(comms);
		restService.unregister();
	}
}
