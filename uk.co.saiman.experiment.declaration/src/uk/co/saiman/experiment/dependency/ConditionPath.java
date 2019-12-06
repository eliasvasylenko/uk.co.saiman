package uk.co.saiman.experiment.dependency;

import java.util.Optional;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public class ConditionPath<T extends ExperimentPath<T>, U> extends ProductPath<T, Condition<U>> {
  ConditionPath(ExperimentPath<T> experimentPath, Class<U> production) {
    super(experimentPath, production);
  }

  @SuppressWarnings("unchecked")
  public Class<U> getPreparation() {
    return (Class<U>) getProduction();
  }

  @Override
  <V extends ExperimentPath<V>> ConditionPath<V, U> moveTo(ExperimentPath<V> experimentPath) {
    return new ConditionPath<>(experimentPath, getPreparation());
  }

  @Override
  public Optional<ConditionPath<Absolute, U>> resolveAgainst(ExperimentPath<Absolute> path) {
    return getExperimentPath().resolveAgainst(path).map(experimentPath -> moveTo(experimentPath));
  }

  @Override
  public ConditionPath<Absolute, U> toAbsolute() {
    return moveTo(getExperimentPath().toAbsolute());
  }
}
