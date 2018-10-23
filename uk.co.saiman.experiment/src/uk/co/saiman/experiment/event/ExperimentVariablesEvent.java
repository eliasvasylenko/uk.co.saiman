package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.STATE;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.state.StateMap;

public class ExperimentVariablesEvent extends ExperimentEvent {
  private final StateMap stateMap;
  private final StateMap previousStateMap;

  public ExperimentVariablesEvent(ExperimentNode<?, ?> node, StateMap previousStateMap) {
    super(node);
    this.stateMap = node.getStateMap();
    this.previousStateMap = previousStateMap;
  }

  public StateMap stateMap() {
    return stateMap;
  }

  public StateMap previousStateMap() {
    return previousStateMap;
  }

  @Override
  public ExperimentEventKind kind() {
    return STATE;
  }
}