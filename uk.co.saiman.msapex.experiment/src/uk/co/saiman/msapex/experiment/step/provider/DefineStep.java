package uk.co.saiman.msapex.experiment.step.provider;

import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.production.Dependency;

public interface DefineStep<T extends Dependency> {
  default StepDefinition<T> withName(String name) {
    return withId(ExperimentId.fromName(name));
  }

  StepDefinition<T> withId(ExperimentId id);
}
