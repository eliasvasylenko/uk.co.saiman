package uk.co.saiman.experiment.dependency;

import java.util.Optional;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public class ResultPath<T extends ExperimentPath<T>, U> extends ProductPath<T, Result<U>> {
  ResultPath(ExperimentPath<T> experimentPath, Class<U> production) {
    super(experimentPath, production);
  }

  @SuppressWarnings("unchecked")
  public Class<U> getObservation() {
    return (Class<U>) getProduction();
  }

  @Override
  <V extends ExperimentPath<V>> ResultPath<V, U> moveTo(ExperimentPath<V> experimentPath) {
    return new ResultPath<>(experimentPath, getObservation());
  }

  @Override
  public Optional<ResultPath<Absolute, U>> resolveAgainst(ExperimentPath<Absolute> path) {
    return getExperimentPath().resolveAgainst(path).map(experimentPath -> moveTo(experimentPath));
  }

  @Override
  public ResultPath<Absolute, U> toAbsolute() {
    return moveTo(getExperimentPath().toAbsolute());
  }
}
