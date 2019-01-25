package uk.co.saiman.experiment;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

public class ExperimentDefinition<S> {
  private final String id;
  private final List<ExperimentDefinition<?>> components;

  public ExperimentDefinition(String id) {
    this(id, emptyList());
  }

  private ExperimentDefinition(String id, List<ExperimentDefinition<?>> components) {
    this.id = id;
    this.components = components;
  }

  public ExperimentDefinition<S> withComponent(ExperimentDefinition<?> step) {
    return withComponent(components.size(), step);
  }

  public ExperimentDefinition<S> withComponent(int index, ExperimentDefinition<?> step) {
    return new ExperimentDefinition<>(id, listWithElement(components, index, step));
  }

  static <T> List<T> listWithElement(List<T> list, int index, T element) {
    List<T> newList = new ArrayList<>(list.size() + 1);
    newList.addAll(list);
    newList.add(index, element);
    return newList;
  }

  static <T> List<T> listWithoutElement(List<T> list, int index) {
    ArrayList<T> newList = new ArrayList<>(list);
    newList.remove(index);
    newList.trimToSize();
    return newList;
  }
}
