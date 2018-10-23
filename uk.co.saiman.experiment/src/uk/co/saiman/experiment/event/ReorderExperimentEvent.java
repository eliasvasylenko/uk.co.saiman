package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.REORDER;

import uk.co.saiman.experiment.ExperimentNode;

public class ReorderExperimentEvent extends ExperimentEvent {
  private final int index;
  private final int previousIndex;

  public ReorderExperimentEvent(ExperimentNode<?, ?> experiment, int previousIndex) {
    super(experiment);
    this.index = experiment.getIndex();
    this.previousIndex = previousIndex;
  }

  public int index() {
    return index;
  }

  public int previousIndex() {
    return previousIndex;
  }

  @Override
  public ExperimentEventKind kind() {
    return REORDER;
  }
}
