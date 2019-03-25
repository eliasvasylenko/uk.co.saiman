package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.REMOVE_STEP;

import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.path.Dependency;

public class RemoveStepEvent extends ExperimentStepEvent {
  @Override
  public ExperimentEventKind kind() {
    return REMOVE_STEP;
  }

  public Dependency<?, ?> previousDependency() {
    // TODO Auto-generated method stub
    return null;
  }

  public Step previousDependencyStep() {
    // TODO Auto-generated method stub
    return null;
  }
}
