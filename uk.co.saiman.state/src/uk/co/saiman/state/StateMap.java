/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.state;

import static java.util.Collections.emptyMap;
import static uk.co.saiman.state.StateKind.MAP;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
    return getOptional(id).orElseThrow(() -> new MissingStateException(id));
  }

  public Optional<State> getOptional(String id) {
    return Optional.ofNullable(entries.get(id));
  }

  public <T> T get(Accessor<T, StateMap> accessor) {
    return accessor.read(this);
  }

  public StateMap with(String id, State value) {
    Map<String, State> entries = new HashMap<>(this.entries);
    entries.put(id, value);
    return new StateMap(entries);
  }

  public <T> StateMap with(String id, Function<State, State> value) {
    return with(id, value.apply(get(id)));
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
  private <T, U extends State> T readAs(Accessor<T, U> accessor, State state) {
    return accessor.read((U) state.as(accessor.getKind()));
  }

  public <T, U extends State> T get(MapIndex<T> index) {
    return readAs(index.accessor(), get(index.id()));
  }

  public <T> Optional<T> getOptional(MapIndex<T> index) {
    return getOptional(index.id()).map(s -> readAs(index.accessor(), s));
  }

  public <T> StateMap with(MapIndex<T> index, T value) {
    return with(index.id(), index.accessor().write(value));
  }

  public <T> StateMap withDefault(MapIndex<T> index, Supplier<? extends T> value) {
    return withDefault(index.id(), () -> index.accessor().write(value.get()));
  }

  public <T> StateMap with(MapIndex<T> index, Function<T, T> value) {
    return with(index.id(), index.accessor().write(value.apply(get(index))));
  }

  public StateMap remove(MapIndex<?> index) {
    return remove(index.id());
  }

  @Override
  public StateKind getKind() {
    return MAP;
  }

  public static StateMap empty() {
    return EMPTY;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof StateMap))
      return false;

    StateMap that = (StateMap) obj;

    return Objects.equals(this.entries, that.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries);
  };
}
