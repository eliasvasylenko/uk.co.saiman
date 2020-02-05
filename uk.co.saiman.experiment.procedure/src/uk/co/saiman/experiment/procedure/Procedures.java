package uk.co.saiman.experiment.procedure;

import static java.util.stream.Collectors.toList;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.environment.service.LocalEnvironmentService;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.instruction.Instruction;

public final class Procedures {
  private Procedures() {}

  public static ProcedureDependents getDependents(Procedure procedure) {
    return new ProcedureDependents(procedure);
  }

  public static LocalEnvironment openEnvironment(
      Procedure procedure,
      LocalEnvironmentService environmentService,
      int time,
      TimeUnit unit) {
    var environment = environmentService.openLocalEnvironment(procedure.environment());
    environment
        .acquireResources(
            Procedures.getResourceDependencies(procedure).collect(toList()),
            time,
            unit);
    return environment;
  }

  public static Optional<ExperimentPath<Absolute>> getConditionDependency(
      Instruction instruction,
      Class<?> source) {
    // TODO Auto-generated method stub
    return null;
  }

  public static Optional<ExperimentPath<Absolute>> getResultDependency(
      Instruction instruction,
      Class<?> source) {
    // TODO Auto-generated method stub
    return null;
  }

  public static Stream<ExperimentPath<Absolute>> getAdditionalResultDependencies(
      Instruction instruction,
      Class<?> source) {
    // TODO Auto-generated method stub
    return null;
  }

  public static Evaluation getPreparedConditionEvaluation(
      Instruction instruction,
      Class<?> source) {
    // TODO Auto-generated method stub

  }
}
