package uk.co.saiman.comms;

import java.util.stream.Stream;

public interface CommsREST {
	Stream<String> getItems();

	CommsRESTItem getItem(String item);
}
