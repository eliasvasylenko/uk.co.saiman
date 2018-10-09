package uk.co.saiman.experiment;

import static uk.co.saiman.experiment.ExperimentEventKind.REORDER;

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
