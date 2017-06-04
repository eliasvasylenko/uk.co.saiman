package uk.co.saiman.experiment;

import java.util.Optional;
import java.util.stream.Stream;

public interface PersistedState {
  Stream<String> getStrings();

  Optional<String> removeString(String key);

  void clear();

  Optional<String> getString(String key);

  Optional<String> putString(String key, String value);
}
