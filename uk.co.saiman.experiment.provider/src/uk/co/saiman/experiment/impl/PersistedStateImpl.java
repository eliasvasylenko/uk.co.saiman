package uk.co.saiman.experiment.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import uk.co.saiman.experiment.PersistedState;
import uk.co.strangeskies.observable.ObservableImpl;

public class PersistedStateImpl extends ObservableImpl<PersistedState> implements PersistedState {
	private final Map<String, String> strings = new HashMap<>();

	private void update() {
		fire(this);
	}

	@Override
	public Stream<String> getStrings() {
		return strings.keySet().stream();
	}

	@Override
	public void removeKey(String key) {
		strings.remove(key);
		update();
	}

	@Override
	public void clear() {
		strings.clear();
		update();
	}

	@Override
	public String getString(String key) {
		return strings.get(key);
	}

	@Override
	public String putString(String key, String value) {
		String previous = strings.put(key, value);
		update();
		return previous;
	}
}
