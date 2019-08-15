package uk.co.saiman.experiment.executor;

import java.util.Optional;

import uk.co.saiman.experiment.requirement.ProductPath;
import uk.co.saiman.experiment.requirement.Production;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableCardinality;
import uk.co.saiman.experiment.variables.VariableDeclaration;

public interface PlanningContext {
  <T> Optional<T> declareVariable(VariableDeclaration<T> declaration);

  default <T> Optional<T> declareVariable(Variable<T> variable, VariableCardinality cardinality) {
    return declareVariable(new VariableDeclaration<>(variable, cardinality));
  }

  void declareMainRequirement(Production<?, ?> production);

  void declareAdditionalRequirement(ProductPath<?, ?> path);

  void declareResourceRequirement(Class<?> type);

  void executesAutomatically();

  void observesResult(Class<?> production);

  default void preparesCondition(Class<?> type) {
    preparesCondition(type, Evaluation.ORDERED);
  }

  void preparesCondition(Class<?> type, Evaluation evaluation);

  interface NoOpPlanningContext extends PlanningContext {
    @Override
    default void declareMainRequirement(Production<?, ?> production) {}

    @Override
    default void declareAdditionalRequirement(ProductPath<?, ?> path) {}

    @Override
    default void declareResourceRequirement(Class<?> type) {}

    @Override
    default void executesAutomatically() {}

    @Override
    default void observesResult(Class<?> production) {}

    @Override
    default void preparesCondition(Class<?> type, Evaluation evaluation) {}
  }
}
