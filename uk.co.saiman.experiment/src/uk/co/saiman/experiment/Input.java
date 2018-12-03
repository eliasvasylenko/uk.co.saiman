package uk.co.saiman.experiment;

import uk.co.saiman.experiment.path.ResultPath;

/**
 * An input is a wiring from a dependency to an observation which satisfies that
 * dependency.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the dependency
 */
public class Input<T> {
  private final ExperimentStep<?> step;
  private final Dependency<T> dependency;
  private ResultPath<T> resultPath;
  private Result<? extends T> result;

  public Input(ExperimentStep<?> step, Dependency<T> dependency) {
    this.step = step;
    this.dependency = dependency;
  }

  public ExperimentStep<?> getExperimentStep() {
    return step;
  }

  public Dependency<T> getDependency() {
    return dependency;
  }

  public void setResultPath(ResultPath<T> resultPath) {
    this.result = resultPath.resolve(step);
    this.resultPath = resultPath;
  }

  public ResultPath<T> getResultPath() {
    return resultPath;
  }

  public Result<? extends T> getResult() {
    return result;
  }
}
