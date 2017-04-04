package uk.co.saiman.comms.impl;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.rest.CommandRESTConverter;

@Component
public class NumberedBitsRESTConverter implements CommandRESTConverter {
	@Override
	public Object convertOutput(Object target, Map<String, Object> output) {
		return null;
	}

	@Override
	public Map<String, Boolean> convertInput(Object input) {
		return null;
	}
}
