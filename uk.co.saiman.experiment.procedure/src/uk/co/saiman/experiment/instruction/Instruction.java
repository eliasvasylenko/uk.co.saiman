package uk.co.saiman.experiment.instruction;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.path.ExperimentPath.Absolute;
import uk.co.saiman.experiment.production.Dependency;
import uk.co.saiman.experiment.variables.Variables;

public class Instruction<T extends Dependency> {
  private final String id;
  private final Variables variables;
  private final Executor<T> executor;
  private final ExperimentPath<Absolute> path;

  public Instruction(
      String id,
      Variables variables,
      Executor<T> executor,
      ExperimentPath<Absolute> path) {
    this.id = id;
    this.variables = variables;
    this.executor = executor;
    this.path = path;
  }

  public String id() {
    return id;
  }

  public Variables variables() {
    return variables;
  }

  public Executor<T> executor() {
    return executor;
  }

  public ExperimentPath<Absolute> path() {
    return path;
  }
}
