package uk.co.saiman.experiment.conductor;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.InstructionPlanningContext;
import uk.co.saiman.experiment.procedure.Procedures;
import uk.co.saiman.experiment.workspace.WorkspaceExperimentPath;

public class IncomingDependencies {
  private final Conductor conductor;
  private final WorkspaceExperimentPath path;

  private IncomingCondition<?> incomingCondition;
  private IncomingResult<?> incomingResult;
  private List<IncomingResult<?>> additionalIncomingResults;

  public IncomingDependencies(Conductor conductor, WorkspaceExperimentPath path) {
    this.conductor = conductor;
    this.path = path;
  }

  void update(Instruction instruction, LocalEnvironment environment) {
    requireNonNull(instruction);
    requireNonNull(environment);

    incomingCondition = null;
    incomingResult = null;
    additionalIncomingResults = List.of();

    Procedures
        .plan(
            instruction,
            environment.getGlobalEnvironment(),
            variables -> new InstructionPlanningContext() {
              @Override
              public void declareConditionRequirement(Class<?> production) {
                if (incomingResult != null || !additionalIncomingResults.isEmpty()) {
                  throw new ConductorException(
                      "Cannot depend on results and conditions at the same time");
                }
                if (incomingCondition != null) {
                  throw new ConductorException(
                      "Cannot declare multiple primary condition requirements");
                }
                incomingCondition = getParent()
                    .map(dependency -> dependency.addConditionConsumer(production, path))
                    .orElse(null);
              }

              @Override
              public void declareResultRequirement(Class<?> production) {
                if (incomingResult != null) {
                  throw new ConductorException(
                      "Cannot declare multiple primary result requirements, use additional result requirements");
                }
                if (incomingCondition != null) {
                  throw new ConductorException(
                      "Cannot depend on results and conditions at the same time");
                }
                incomingResult = getParent()
                    .map(dependency -> dependency.addResultConsumer(production, path))
                    .orElse(null);
              }

              @Override
              public void declareAdditionalResultRequirement(ResultPath<?, ?> path) {
                if (incomingCondition != null) {
                  throw new ConductorException(
                      "Cannot depend on results and conditions at the same time");
                }
                getParent()
                    .ifPresent(
                        dependency -> additionalIncomingResults
                            .add(
                                dependency
                                    .addResultConsumer(
                                        path.getProduction(),
                                        IncomingDependencies.this.path)));
              }
            });
  }

  protected Optional<InstructionExecution> getParent() {
    return path
        .getExperimentPath()
        .parent()
        .map(p -> WorkspaceExperimentPath.define(path.getExperimentId(), p))
        .flatMap(conductor::findInstruction);
  }

  @SuppressWarnings("unchecked")
  public <T> Condition<T> acquireCondition(Class<T> source) {
    if (incomingCondition == null || incomingCondition.type() != source) {
      throw new ConductorException("No condition dependency declared on " + source);
    }
    return ((IncomingCondition<T>) incomingCondition).acquire();
  }

  @SuppressWarnings("unchecked")
  public <T> Result<T> acquireResult(Class<T> source) {
    if (incomingResult == null || incomingResult.type() != source) {
      throw new ConductorException("No result dependency declared on " + source);
    }
    return ((IncomingResult<T>) incomingResult).acquire();
  }

  @SuppressWarnings("unchecked")
  public <T> Stream<Result<T>> acquireAdditionalResults(Class<T> source) {
    return Optional
        .ofNullable(additionalIncomingResults)
        .stream()
        .flatMap(List::stream)
        .filter(r -> r.type() == source)
        .map(r -> (IncomingResult<T>) r)
        .map(IncomingResult::acquire)
        .collect(toList())
        .stream();
  }

  public void terminate() {
    if (incomingCondition != null) {
      incomingCondition.done();
    }
    if (incomingResult != null) {
      incomingResult.done();
    }
    if (additionalIncomingResults != null) {
      for (var resultDependency : additionalIncomingResults) {
        resultDependency.done();
      }
    }
  }

  public void invalidate() {
    if (incomingCondition != null) {
      incomingCondition.invalidateIncoming();
    }
    if (incomingResult != null) {
      incomingResult.invalidateIncoming();
    }
    if (additionalIncomingResults != null) {
      for (var resultDependency : additionalIncomingResults) {
        resultDependency.invalidateIncoming();
      }
    }
  }
}
