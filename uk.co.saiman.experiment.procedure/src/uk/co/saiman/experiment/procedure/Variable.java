package uk.co.saiman.experiment.procedure;

import uk.co.saiman.state.Accessor;

public class Variable<T> {
  private final Class<T> variableType;
  private final Accessor<T, ?> accessor;

  public Variable(Class<T> variableType, Accessor<T, ?> accessor) {
    this.variableType = variableType;
    this.accessor = accessor;
  }

  /**
   * @return the type of the variable
   */
  public Class<T> variableType() {
    return variableType;
  }

  public Accessor<T, ?> accessor() {
    return accessor;
  }
}
