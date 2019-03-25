package uk.co.saiman.experiment.procedure;

import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.MapIndex;

public class Variable<T> {
  private final String name;
  private final Class<? super T> type;
  private final Accessor<T, ?> accessor;

  public Variable(String name, Class<? super T> type, Accessor<T, ?> accessor) {
    this.name = name;
    this.type = type;
    this.accessor = accessor;
  }

  public String name() {
    return name;
  }

  /**
   * @return the type of the variable
   */
  public Class<? super T> type() {
    return type;
  }

  public MapIndex<T> index() {
    return new MapIndex<>(name, accessor);
  }
}
