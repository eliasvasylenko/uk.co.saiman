package uk.co.saiman.experiment.procedure;

import uk.co.saiman.experiment.dependency.ResultPath;
import uk.co.saiman.experiment.executor.Evaluation;
import uk.co.saiman.experiment.variables.VariableDeclaration;

public interface InstructionPlanningContext {
  default <T> void declareVariable(VariableDeclaration<T> declaration) {}

  default void declareResultRequirement(Class<?> production) {}

  default void declareConditionRequirement(Class<?> production) {}

  default void declareAdditionalResultRequirement(ResultPath<?, ?> path) {}

  default void declareResourceRequirement(Class<?> type) {}

  default void executesAutomatically() {}

  default void observesResult(Class<?> type) {}

  default void preparesCondition(Class<?> type, Evaluation evaluation) {}
}
