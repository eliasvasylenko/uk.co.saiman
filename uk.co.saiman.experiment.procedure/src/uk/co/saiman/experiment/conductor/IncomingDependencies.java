package uk.co.saiman.experiment.conductor;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.conductor.OutgoingResults.ResultObservation.IncomingResult;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.Result;
import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.environment.LocalEnvironment;
import uk.co.saiman.experiment.instruction.Instruction;
import uk.co.saiman.experiment.procedure.InstructionPlanningContext;
import uk.co.saiman.experiment.procedure.Procedures;

public class IncomingDependencies {
  private final Conductor conductor;
  private final ExperimentPath<Absolute> path;

  private IncomingCondition<?> incomingCondition;
  private IncomingResult<?> incomingResult;
  private List<IncomingResult<?>> additionalIncomingResults;

  public IncomingDependencies(Conductor conductor, ExperimentPath<Absolute> path) {
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
                    .map(dependency -> dependency.addConditionConsumer(production))
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
                    .map(dependency -> dependency.addResultConsumer(production))
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
                            .add(dependency.addResultConsumer(path.getProduction())));
              }
            });
  }

  protected Optional<InstructionExecution> getParent() {
    return path.parent().flatMap(conductor::findInstruction);
  }

  @SuppressWarnings("unchecked")
  public <T> Condition<T> acquireCondition(Class<T> source) {
    if (incomingCondition == null || incomingCondition.outgoingPath().getProduction() != source) {
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
}
