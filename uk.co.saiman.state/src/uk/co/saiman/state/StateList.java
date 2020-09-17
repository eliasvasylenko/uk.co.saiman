/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.state.
 *
 * uk.co.saiman.state is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.state is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.state;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.state.StateKind.LIST;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class StateList implements State, Iterable<State> {
  private static final StateList EMPTY = new StateList(emptyList());

  private final List<State> elements;

  private StateList(List<State> elements) {
    this.elements = elements;
  }

  @Override
  public Iterator<State> iterator() {
    return elements.iterator();
  }

  public State get(int index) {
    return elements.get(index);
  }

  public Optional<State> getOptional(int index) {
    return index >= 0 && index < elements.size()
        ? Optional.of(elements.get(index))
        : Optional.empty();
  }

  public <T> T get(Accessor<T, StateList> accessor) {
    return accessor.read(this);
  }

  public StateList withAdded(State element) {
    List<State> elements = new ArrayList<>(this.elements);
    elements.add(element);
    return new StateList(elements);
  }

  public StateList withAdded(int index, State element) {
    List<State> elements = new ArrayList<>(this.elements);
    elements.add(index, element);
    return new StateList(elements);
  }

  public StateList withSet(int index, State element) {
    List<State> elements = this.elements;
    elements.set(index, element);
    return new StateList(elements);
  }

  public StateList remove(State element) {
    List<State> elements = new ArrayList<>(this.elements);
    if (elements.remove(element)) {
      return new StateList(elements);
    } else {
      return this;
    }
  }

  public StateList remove(int index) {
    List<State> elements = new ArrayList<>(this.elements);
    elements.remove(index);
    return new StateList(elements);
  }

  @SuppressWarnings("unchecked")
  private <T, U extends State> T readAs(Accessor<T, U> accessor, State state) {
    return accessor.read((U) state.as(accessor.getKind()));
  }

  public <T, U extends State> T get(ListIndex<T> index) {
    return readAs(index.accessor(), get(index.position()));
  }

  public <T> Optional<T> getOptional(ListIndex<T> index) {
    return getOptional(index.position()).map(s -> readAs(index.accessor(), s));
  }

  public <T> StateList withSet(ListIndex<T> index, T value) {
    return withSet(index.position(), index.accessor().write(value));
  }

  public <T> StateList withSet(ListIndex<T> index, Function<T, T> value) {
    return withSet(index.position(), index.accessor().write(value.apply(get(index))));
  }

  public <T> StateList withAdded(ListIndex<T> index, T value) {
    return withSet(index.position(), index.accessor().write(value));
  }

  public <T> StateList withAdded(Accessor<T, ?> accessor, T value) {
    return withAdded(accessor.write(value));
  }

  public StateList remove(ListIndex<?> index) {
    return remove(index.position());
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public int size() {
    return elements.size();
  }

  public Stream<State> stream() {
    return elements.stream();
  }

  @Override
  public StateKind getKind() {
    return LIST;
  }

  public static StateList empty() {
    return EMPTY;
  }

  public static Collector<State, ?, StateList> toStateList() {
    return collectingAndThen(toList(), StateList::new);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof StateList))
      return false;

    StateList that = (StateList) obj;

    return Objects.equals(this.elements, that.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }

  @Override
  public String toString() {
    return elements.toString();
  }
}
