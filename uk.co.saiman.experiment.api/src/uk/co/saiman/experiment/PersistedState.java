package uk.co.saiman.experiment;

import java.util.stream.Stream;

public interface PersistedState {
	Stream<String> getStrings();

	void removeKey(String key);

	void clear();

	String getString(String key);

	String putString(String key, String value);
}
