package uk.co.saiman.experiment;

import java.util.stream.Stream;

public interface PersistedStateList extends Iterable<PersistedState> {
  PersistedState add();

  PersistedState add(int index);

  PersistedState get(int index);

  PersistedState remove(int index);

  int size();

  Stream<PersistedState> stream();
}
