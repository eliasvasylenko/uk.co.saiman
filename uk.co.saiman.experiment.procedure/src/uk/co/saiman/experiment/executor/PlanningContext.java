package uk.co.saiman.experiment.executor;

import java.util.Optional;

import uk.co.saiman.experiment.dependency.ProductPath;
import uk.co.saiman.experiment.dependency.source.Production;
import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableCardinality;
import uk.co.saiman.experiment.variables.VariableDeclaration;

public interface PlanningContext {
  <T> Optional<T> declareVariable(VariableDeclaration<T> declaration);

  default <T> Optional<T> declareVariable(Variable<T> variable, VariableCardinality cardinality) {
    return declareVariable(new VariableDeclaration<>(variable, cardinality));
  }

  void declareMainRequirement(Production<?> production);

  void declareAdditionalRequirement(ProductPath<?, ?> path);

  void declareResourceRequirement(Provision<?> source);

  void requestAutomaticExecution();

  void declareProduct(Production<?> production);

  interface NoOpPlanningContext extends PlanningContext {
    @Override
    default void declareMainRequirement(Production<?> production) {}

    @Override
    default void declareAdditionalRequirement(ProductPath<?, ?> path) {}

    @Override
    default void declareResourceRequirement(Provision<?> source) {}

    @Override
    default void requestAutomaticExecution() {}

    @Override
    default void declareProduct(Production<?> production) {}
  }
}
