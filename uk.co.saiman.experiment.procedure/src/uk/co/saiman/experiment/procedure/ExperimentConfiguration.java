package uk.co.saiman.experiment.procedure;

import uk.co.saiman.state.StateMap;

public class ExperimentConfiguration<T> implements DependencyConfiguration {
  private final T variables;
  private final DependencyConfiguration dependencies;

  public ExperimentConfiguration(T variables) {
    this.variables = variables;
    this.dependencies = DependencyConfiguration.empty();
  }

  public ExperimentConfiguration(T variables, DependencyConfiguration dependencies) {
    this.variables = variables;
    this.dependencies = dependencies;
  }

  public T variables() {
    return variables;
  }

  @Override
  public Dependencies loadDependencies(StateMap state) {
    return dependencies.loadDependencies(state);
  }
}
