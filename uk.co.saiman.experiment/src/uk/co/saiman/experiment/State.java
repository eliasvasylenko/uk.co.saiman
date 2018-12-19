package uk.co.saiman.experiment;

import java.util.HashSet;
import java.util.Set;

public class State {
  private final ExperimentStep<?> experimentStep;
  private final Condition condition;
  private final Set<Hold> holds;

  public State(ExperimentStep<?> experimentStep, Condition condition) {
    this.experimentStep = experimentStep;
    this.condition = condition;
    this.holds = new HashSet<>();
  }

  public ExperimentStep<?> getExperimentStep() {
    return experimentStep;
  }

  public Condition getCondition() {
    return condition;
  }

  void enter() {
    // TODO Auto-generated method stub

  }

  void exit() {
    // TODO Auto-generated method stub

  }

  Hold takeHold() {
    var hold = new Hold(this);
    holds.add(hold);
    return hold;
  }

  void releaseHold(Hold hold) {
    holds.remove(hold);
  }
}
