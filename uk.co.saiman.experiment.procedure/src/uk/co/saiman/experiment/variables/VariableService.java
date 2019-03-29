package uk.co.saiman.experiment.variables;

import uk.co.saiman.state.StateMap;

public interface VariableService {
  default Variables createVariables() {
    return createVariables(StateMap.empty());
  }

  Variables createVariables(StateMap stateMap);
}
