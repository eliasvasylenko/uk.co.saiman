package uk.co.saiman.experiment.variables;

import uk.co.saiman.state.Accessor;
import uk.co.saiman.state.MapIndex;

public class VariableSupplier<T> {
  private final String supplierId;
  private final Variable<T> variable;
  private final Accessor<T, ?> accessor;

  public VariableSupplier(String supplierId, Variable<T> variable, Accessor<T, ?> accessor) {
    this.supplierId = supplierId;
    this.variable = variable;
    this.accessor = accessor;
  }

  public String supplierId() {
    return supplierId;
  }

  public Variable<T> variable() {
    return variable;
  }

  public MapIndex<T> accessor() {
    return new MapIndex<>(supplierId, accessor);
  }
}
