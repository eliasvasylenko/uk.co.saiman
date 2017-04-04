package uk.co.saiman.comms.impl;

import static java.lang.Enum.valueOf;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.NamedBits;
import uk.co.saiman.comms.rest.CommandRESTConverter;

@Component
public class NamedBitsRESTConverter implements CommandRESTConverter {
	@Override
	public Object convertOutput(Object target, Map<String, Object> output) {
		if (target instanceof NamedBits<?>) {
			NamedBits<?> object = (NamedBits<?>) target;
			for (String item : output.keySet()) {
				object = withSet(object, item, output);
			}
			return object;
		}

		return null;
	}

	private <T extends Enum<T>> NamedBits<?> withSet(
			NamedBits<T> object,
			String item,
			Map<String, Object> output) {
		return object.withSet(valueOf(object.getBitClass(), item), (Boolean) output.get(item));
	}

	@Override
	public Map<String, Boolean> convertInput(Object input) {
		if (input instanceof NamedBits<?>) {
			return ((NamedBits<?>) input)
					.toMap()
					.entrySet()
					.stream()
					.collect(toMap(e -> e.getKey().name(), Entry::getValue));
		}

		return null;
	}
}
