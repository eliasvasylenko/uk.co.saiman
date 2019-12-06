package uk.co.saiman.experiment.conductor;

import static java.util.stream.Collectors.toMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.environment.GlobalEnvironment;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.experiment.variables.Variables;

public class ProcedureDependents {
  enum DependencyKind {
    RESULT, CONDITION
  }

  static class Dependency {
    private final DependencyKind kind;
    private final Class<?> production;
    private final ExperimentPath<Absolute> from;
    private final ExperimentPath<Absolute> to;

    public Dependency(
        DependencyKind kind,
        Class<?> production,
        ExperimentPath<Absolute> from,
        ExperimentPath<Absolute> to) {
      this.kind = kind;
      this.production = production;
      this.from = from;
      this.to = to;
    }

    public DependencyKind kind() {
      return kind;
    }

    public Class<?> production() {
      return production;
    }

    public ExperimentPath<Absolute> from() {
      return from;
    }

    public ExperimentPath<Absolute> to() {
      return to;
    }
  }

  private final Map<ExperimentPath<Absolute>, InstructionDependents> instructionDependents;

  public ProcedureDependents(Procedure procedure) {
    instructionDependents = procedure
        .instructions()
        .flatMap(instruction -> getDependencies(procedure.environment(), instruction))
        .collect(
            toMap(
                Dependency::to,
                dependency -> with(new InstructionDependents(), dependency),
                InstructionDependents::merge));
  }

  public InstructionDependents getInstructionDependents(ExperimentPath<Absolute> path) {
    return instructionDependents.get(path);
  }

  static InstructionDependents with(InstructionDependents dependents, Dependency dependency) {
    return dependency.kind() == DependencyKind.CONDITION
        ? dependents.withConditionDependent(dependency.production(), dependency.from())
        : dependents.withResultDependent(dependency.production(), dependency.from());
  }

  static Stream<Dependency> getDependencies(
      GlobalEnvironment environment,
      Instruction instruction) {
    Set<Dependency> instructionDependencies = new HashSet<>();

    var planningContext = new PlanningContext.NoOpPlanningContext() {
      private final Variables variables = new Variables(environment, instruction.variableMap());

      @Override
      public <T> Optional<T> declareVariable(VariableDeclaration<T> declaration) {
        return variables.get(declaration.variable());
      }

      @Override
      public void declareResultRequirement(Class<?> production) {
        instruction
            .path()
            .parent()
            .ifPresent(
                dependency -> instructionDependencies
                    .add(
                        new Dependency(
                            DependencyKind.RESULT,
                            production,
                            instruction.path(),
                            dependency)));
      }

      @Override
      public void declareConditionRequirement(Class<?> production) {
        instruction
            .path()
            .parent()
            .ifPresent(
                dependency -> instructionDependencies
                    .add(
                        new Dependency(
                            DependencyKind.CONDITION,
                            production,
                            instruction.path(),
                            dependency)));
      }

      @Override
      public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
        path
            .getExperimentPath()
            .resolveAgainst(instruction.path())
            .ifPresent(
                dependency -> instructionDependencies
                    .add(
                        new Dependency(
                            DependencyKind.RESULT,
                            path.getProduction(),
                            instruction.path(),
                            dependency)));
      }
    };

    planningContext.useOnce(instruction.executor());

    return instructionDependencies.stream();
  }
}
