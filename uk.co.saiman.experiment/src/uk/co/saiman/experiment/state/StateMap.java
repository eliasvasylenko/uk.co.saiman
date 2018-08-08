package uk.co.saiman.experiment.state;

import static java.util.Collections.emptyMap;
import static uk.co.saiman.experiment.state.StateKind.MAP;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StateMap implements State {
  private static final StateMap EMPTY = new StateMap(emptyMap());

  private final Map<String, State> entries;

  private StateMap(Map<String, State> entries) {
    this.entries = entries;
  }

  public boolean isEmpty() {
    return entries.isEmpty();
  }

  public Stream<String> getKeys() {
    return entries.keySet().stream();
  }

  public State get(String id) {
    return entries.get(id);
  }

  public StateMap with(String id, State value) {
    Map<String, State> entries = new HashMap<>(this.entries);
    entries.put(id, value);
    return new StateMap(entries);
  }

  public StateMap withDefault(String id, State value) {
    return withDefault(id, () -> value);
  }

  public StateMap withDefault(String id, Supplier<? extends State> value) {
    if (entries.containsKey(id)) {
      return this;
    } else {
      return with(id, value.get());
    }
  }

  public StateMap remove(String id) {
    if (!entries.containsKey(id)) {
      return this;
    } else {
      Map<String, State> entries = new HashMap<>(this.entries);
      entries.remove(id);
      return new StateMap(entries);
    }
  }

  @SuppressWarnings("unchecked")
  public <T, U extends State> T get(Accessor<T, U> accessor) {
    return accessor.read((U) get(accessor.id()).as(accessor.getKind()));
  }

  public <T> StateMap with(Accessor<T, ?> accessor, T value) {
    return with(accessor.id(), accessor.write(value));
  }

  public <T> StateMap withDefault(Accessor<T, ?> accessor, T value) {
    return withDefault(accessor, (Supplier<T>) () -> value);
  }

  public <T> StateMap withDefault(Accessor<T, ?> accessor, Supplier<? extends T> value) {
    return withDefault(accessor.id(), () -> accessor.write(value.get()));
  }

  public StateMap remove(Accessor<?, ?> accessor) {
    return remove(accessor.id());
  }

  @Override
  public StateKind getKind() {
    return MAP;
  }

  public static StateMap empty() {
    return EMPTY;
  }
}
