package uk.co.saiman.experiment.variables;

public class VariableDeclaration {
  private final Variable<?> variable;
  private final VariableCardinality cardinality;

  VariableDeclaration(Variable<?> variable, VariableCardinality cardinality) {
    this.variable = variable;
    this.cardinality = cardinality;
  }

  public Variable<?> variable() {
    return variable;
  }

  public VariableCardinality cardinality() {
    return cardinality;
  }
}
