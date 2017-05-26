package uk.co.saiman.comms;

import java.util.Map;
import java.util.stream.Stream;

public interface CommsRESTItem {
	Map<String, Object> invokeInputAction();

	Map<String, Object> getOutputObject();

	Stream<String> getActions();

	void invokeAction(String action);
}
