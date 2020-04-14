package uk.co.saiman.experiment.procedure;

import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.environment.Environment;
import uk.co.saiman.experiment.environment.ResourceMissingException;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.PlanningContext;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.experiment.variables.Variables;

public final class Procedures {
  private Procedures() {}

  public static void validateEnvironment(Procedure procedure, Environment environment) {
    var resources = environment.resources().collect(Collectors.toSet());
    procedure
        .instructions()
        .flatMap(instruction -> getResourceDependencies(instruction, procedure.environment()))
        .filter(not(resources::contains))
        .map(ResourceMissingException::new)
        .reduce((e1, e2) -> {
          e1.addSuppressed(e2);
          return e1;
        })
        .ifPresent(e -> {
          throw e;
        });
  }

  public static Stream<Class<?>> getObservations(Instruction instruction, Environment environment) {
    var observations = new ArrayList<Class<?>>();
    plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public void observesResult(Class<?> production) {
        observations.add(production);
      }
    });
    return observations.stream();
  }

  public static Stream<Class<?>> getPreparations(Instruction instruction, Environment environment) {
    var preparations = new ArrayList<Class<?>>();
    plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        preparations.add(type);
      }
    });
    return preparations.stream();
  }

  public static Stream<VariableDeclaration<?>> getVariableDeclarations(
      Instruction instruction,
      Environment environment) {
    var declarations = new ArrayList<VariableDeclaration<?>>();
    plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public <T> void declareVariable(VariableDeclaration<T> declaration) {
        declarations.add(declaration);
      }
    });
    return declarations.stream();
  }

  public static Stream<Class<?>> getResourceDependencies(
      Instruction instruction,
      Environment environment) {
    var resources = new ArrayList<Class<?>>();
    plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public void declareResourceRequirement(Class<?> type) {
        resources.add(type);
      }
    });
    return resources.stream();
  }

  public static Stream<Class<?>> getPreparedConditions(
      Instruction instruction,
      Environment environment) {
    var conditions = new ArrayList<Class<?>>();
    plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        conditions.add(type);
      }
    });
    return conditions.stream();
  }

  public static Stream<Class<?>> getObservedResults(
      Instruction instruction,
      Environment environment) {
    var results = new ArrayList<Class<?>>();
    plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public void observesResult(Class<?> type) {
        results.add(type);
      }
    });
    return results.stream();
  }

  public static Optional<Evaluation> getPreparedConditionEvaluation(
      Instruction instruction,
      Environment environment,
      Class<?> source) {
    var conditions = new ArrayList<Evaluation>();
    plan(instruction, environment, variables -> new InstructionPlanningContext() {
      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        if (type == source) {
          conditions.clear();
          conditions.add(evaluation);
        }
      }
    });
    return conditions.stream().findAny();
  }

  public static void plan(Executor executor, PlanningContext context) {
    var safeContext = new PlanningContext() {
      private boolean done = false;

      private void assertLive() {
        if (done) {
          throw new IllegalStateException();
        }
      }

      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        assertLive();
        context.preparesCondition(type, evaluation);
      }

      @Override
      public void observesResult(Class<?> production) {
        assertLive();
        context.observesResult(production);
      }

      @Override
      public void executesAutomatically() {
        assertLive();
        context.executesAutomatically();
      }

      @Override
      public <T> Optional<T> declareVariable(VariableDeclaration<T> declaration) {
        assertLive();
        return context.declareVariable(declaration);
      }

      @Override
      public void declareResultRequirement(Class<?> production) {
        assertLive();
        context.declareResultRequirement(production);
      }

      @Override
      public void declareResourceRequirement(Class<?> type) {
        assertLive();
        context.declareResourceRequirement(type);
      }

      @Override
      public void declareConditionRequirement(Class<?> production) {
        assertLive();
        context.declareConditionRequirement(production);
      }

      @Override
      public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
        assertLive();
        declareAdditionalResultRequirement(path);
      }
    };
    executor.plan(safeContext);
    safeContext.done = true;
  }

  public static void plan(
      Instruction instruction,
      Environment environment,
      Function<Variables, InstructionPlanningContext> planner) {
    var variables = new Variables(environment, instruction.variableMap());
    var context = planner.apply(variables);

    plan(instruction.executor(), new PlanningContext() {
      @Override
      public void preparesCondition(Class<?> type, Evaluation evaluation) {
        context.preparesCondition(type, evaluation);
      }

      @Override
      public void observesResult(Class<?> production) {
        context.observesResult(production);
      }

      @Override
      public void executesAutomatically() {
        context.executesAutomatically();
      }

      @Override
      public <T> Optional<T> declareVariable(VariableDeclaration<T> declaration) {
        return variables.get(declaration.variable());
      }

      @Override
      public void declareResultRequirement(Class<?> production) {
        context.declareResultRequirement(production);
      }

      @Override
      public void declareResourceRequirement(Class<?> type) {
        context.declareResourceRequirement(type);
      }

      @Override
      public void declareConditionRequirement(Class<?> production) {
        context.declareConditionRequirement(production);
      }

      @Override
      public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
        context.declareAdditionalResultRequirement(path);
      }
    });
  }

  public static Procedure validateDependencies(Procedure procedure) {
    return procedure;
  }
}
