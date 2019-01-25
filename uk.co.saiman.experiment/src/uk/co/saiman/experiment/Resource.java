package uk.co.saiman.experiment;

import static uk.co.saiman.experiment.ExperimentStep.lockExperiments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class Resource {
  private final ExperimentStep<?> step;
  private final List<ExperimentStep<?>> dependentSteps;

  Resource(ExperimentStep<?> step) {
    this.step = step;
    this.dependentSteps = new ArrayList<>();
  }

  public ExperimentStep<?> getNode() {
    return step;
  }

  public void attach(ExperimentStep<?> node) {
    lockExperiments(step, node).update(lock -> attachImpl(step, (int) getComponentSteps().count()));
  }

  public void attach(ExperimentStep<?> node, int index) {
    lockExperiments(step, node).update(lock -> step.attachImpl(step, index));
  }

  Stream<ExperimentStep<?>> getDependentSteps() {
    return dependentSteps.stream();
  }
}
