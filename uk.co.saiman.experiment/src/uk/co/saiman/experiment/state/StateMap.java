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
package uk.co.saiman.experiment.state;

import static java.util.Collections.emptyMap;
import static uk.co.saiman.experiment.state.StateKind.MAP;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

  public Optional<State> getOptional(String id) {
    return Optional.ofNullable(entries.get(id));
  }

  public StateMap with(String id, State value) {
    Map<String, State> entries = new HashMap<>(this.entries);
    entries.put(id, value);
    return new StateMap(entries);
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

  @SuppressWarnings("unchecked")
  public <T, U extends State> Optional<T> getOptional(Accessor<T, U> accessor) {
    return getOptional(accessor.id()).map(s -> accessor.read((U) s.as(accessor.getKind())));
  }

  public <T> StateMap with(Accessor<T, ?> accessor, T value) {
    return with(accessor.id(), accessor.write(value));
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
