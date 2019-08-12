package uk.co.saiman.experiment.variables;

public class VariableDeclaration<T> {
  private final Variable<T> variable;
  private final VariableCardinality cardinality;

  public VariableDeclaration(Variable<T> variable, VariableCardinality cardinality) {
    this.variable = variable;
    this.cardinality = cardinality;
  }

  public Variable<T> variable() {
    return variable;
  }

  public VariableCardinality cardinality() {
    return cardinality;
  }
}
