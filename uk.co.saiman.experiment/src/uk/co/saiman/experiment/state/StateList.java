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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.state.StateKind.LIST;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
}
