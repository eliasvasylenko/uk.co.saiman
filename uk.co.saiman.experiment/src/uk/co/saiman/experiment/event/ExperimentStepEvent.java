package uk.co.saiman.experiment.event;

import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.procedure.Instruction;

public abstract class ExperimentStepEvent extends ExperimentEvent {
  @Override
  public abstract ExperimentEventKind kind();

  public ExperimentPath<Absolute> path() {
    // TODO Auto-generated method stub
    return null;
  }

  public Instruction instruction() {
    // TODO Auto-generated method stub
    return null;
  }

  public Step step() {
    // TODO Auto-generated method stub
    return null;
  }
}
