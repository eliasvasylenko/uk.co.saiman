package uk.co.saiman.experiment;

import static uk.co.saiman.experiment.ExperimentEventKind.LIFECYLE;

public class ExperimentLifecycleEvent extends ExperimentEvent {
  private final ExperimentLifecycleState lifecycleState;
  private final ExperimentLifecycleState previousLifecycleState;

  public ExperimentLifecycleEvent(
      ExperimentNode<?, ?> node,
      ExperimentLifecycleState lifecycleState,
      ExperimentLifecycleState previousLifecycleState) {
    super(node);
    this.lifecycleState = lifecycleState;
    this.previousLifecycleState = previousLifecycleState;
  }

  public ExperimentLifecycleState lifecycleState() {
    return lifecycleState;
  }

  public ExperimentLifecycleState previousLifecycleState() {
    return previousLifecycleState;
  }

  @Override
  public ExperimentEventKind kind() {
    return LIFECYLE;
  }
}