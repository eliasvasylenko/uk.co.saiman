package uk.co.saiman.experiment.procedure;

import uk.co.saiman.state.StateMap;

public interface DependencyConfiguration {
  Dependencies loadDependencies(StateMap state);

  static DependencyConfiguration empty() {
    return s -> Dependencies.none();
  }
}
