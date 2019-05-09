package uk.co.saiman.msapex.experiment;

import static java.util.stream.IntStream.iterate;

import java.util.function.Predicate;

import uk.co.saiman.experiment.definition.StepContainer;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.graph.ExperimentId;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Dependency;
import uk.co.saiman.msapex.experiment.step.provider.DefineStep;

public class DefineStepImpl<T extends Dependency> implements DefineStep<T> {
  private final Predicate<ExperimentId> validate;
  private final Executor<T> executor;

  public DefineStepImpl(StepContainer<?, ?> target, Executor<T> executor) {
    this(id -> target.findSubstep(id).isEmpty(), executor);
  }

  protected DefineStepImpl(Predicate<ExperimentId> validate, Executor<T> executor) {
    this.validate = validate;
    this.executor = executor;
  }

  @Override
  public StepDefinition<T> withId(ExperimentId id) {
    if (validate.test(id)) {
      return StepDefinition.define(id, executor);
    }

    return StepDefinition
        .define(
            iterate(0, i -> ++i)
                .mapToObj(i -> ExperimentId.fromName(id.name() + " " + i))
                .filter(validate)
                .findFirst()
                .get(),
            executor);
  }
}
