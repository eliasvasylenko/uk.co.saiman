package uk.co.saiman.experiment.persistence;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.persistence.StateKind.LIST;

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
