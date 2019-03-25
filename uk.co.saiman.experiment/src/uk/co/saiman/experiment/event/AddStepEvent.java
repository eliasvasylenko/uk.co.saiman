package uk.co.saiman.experiment.event;

import static uk.co.saiman.experiment.event.ExperimentEventKind.ADD_STEP;

import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.path.Dependency;

public class AddStepEvent extends ExperimentStepEvent {
  @Override
  public ExperimentEventKind kind() {
    return ADD_STEP;
  }

  public Dependency<?, ?> dependency() {
    // TODO Auto-generated method stub
    return null;
  }

  public Step dependencyStep() {
    // TODO Auto-generated method stub
    return null;
  }
}
