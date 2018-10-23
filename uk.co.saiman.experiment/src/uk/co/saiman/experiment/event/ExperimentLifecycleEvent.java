package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.LIFECYLE;

import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;

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