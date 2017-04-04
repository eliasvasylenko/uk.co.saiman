package uk.co.saiman.comms.rest;

import java.util.Map;

/**
 * TODO to simplify implementation for now this is just a Map conversion. In the
 * future this may be updated to base the implementation of the OSGi r7
 * converter specification.
 * 
 * @author Elias N Vasylenko
 */
public interface CommandRESTConverter {
	Object convertOutput(Object target, Map<String, Object> output);

	Map<String, ? extends Object> convertInput(Object input);
}
